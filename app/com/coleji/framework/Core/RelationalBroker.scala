package com.coleji.framework.Core

import com.coleji.framework.IO.PreparedQueries._
import com.coleji.framework.Storable.FieldValues.FieldValue
import com.coleji.framework.Storable.Fields.{NullableDateDatabaseField, NullableIntDatabaseField, NullableStringDatabaseField, _}
import com.coleji.framework.Storable.StorableQuery._
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Profiler

import java.security.MessageDigest
import java.sql._
import java.time.{LocalDate, LocalDateTime, ZoneId}
import scala.collection.mutable.ListBuffer

abstract class RelationalBroker private[Core](dbGateway: DatabaseGateway, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends PersistenceBroker(dbGateway, preparedQueriesOnly, readOnly)
{
	//implicit val pb: PersistenceBroker = this

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		val pool = if (pq.useTempSchema) {
			println("using temp schema")
			dbGateway.tempPool
		} else {
			println("using main schema")
			dbGateway.mainPool
		}
		pool.withConnection(c => {
			val profiler = new Profiler
			val rs: ResultSet = pq match {
				case p: PreparedQueryForSelect[T] => {
					println("executing prepared select:")
					println(pq.getQuery)
					val preparedStatement = c.prepareStatement(pq.getQuery)
					p.getParams.zipWithIndex.foreach(t => t._1.set(preparedStatement)(t._2+1))
					println("Parameterized with " + p.getParams)
					preparedStatement.executeQuery
				}
				case _ => {
					println("executing non-prepared select:")
					println(pq.getQuery)
					val st: Statement = c.createStatement()
					st.executeQuery(pq.getQuery)
				}
			}

			rs.setFetchSize(fetchSize)

			val resultObjects: ListBuffer[T] = ListBuffer()
			var rowCounter = 0
			profiler.lap("starting rows")
			while (rs.next) {
				rowCounter += 1
				resultObjects += pq.mapResultSetRowToCaseObject(ResultSetWrapper(rs))
			}
			profiler.lap("finsihed rows")
			val fetchCount: Int = Math.ceil(rowCounter.toDouble / fetchSize.toDouble).toInt
			if (fetchCount > 2) println(" ***********  QUERY EXECUTED " + fetchCount + " FETCHES!!  Rowcount was " + rowCounter + ":  " + pq.getQuery)
			resultObjects.toList
		})
	}

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String] = pq match {
		case p: PreparedQueryForInsert => {
			if (p.preparedParamsBatch.isEmpty) executeSQLForInsert(p.getQuery, p.pkName, p.useTempSchema, Some(p.getParams), None)
			else executeSQLForInsert(p.getQuery, p.pkName, p.useTempSchema, None, Some(p.preparedParamsBatch))
		}
		case hq: HardcodedQueryForInsert => executeSQLForInsert(hq.getQuery, hq.pkName, hq.useTempSchema)
	}

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int = {
		pq match {
			case p: PreparedQueryForUpdateOrDelete => executeSQLForUpdateOrDelete(pq.getQuery, pq.useTempSchema, Some(p.asInstanceOf[PreparedQueryForUpdateOrDelete].getParams))
			case _ => executeSQLForUpdateOrDelete(pq.getQuery, pq.useTempSchema)
		}
	}

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] = {
		val profiler = new Profiler
		val fieldsToGet = fields match{
			case None => obj.fieldList
			case Some(fl) => {
				val fieldNames = fl.map(_.getPersistenceFieldName)
				obj.fieldList.filter(f => fieldNames.contains(f.getPersistenceFieldName))
			}
		}
		profiler.lap("did intersect")
		val sb: StringBuilder = new StringBuilder
		sb.append("SELECT ")
		sb.append(fieldsToGet.map(f => f.getPersistenceFieldName).mkString(", "))
		sb.append(" FROM " + obj.entityName)
		val rows: List[ProtoStorable] = getProtoStorablesFromSelect(sb.toString(), List.empty, fieldsToGet.map(f => ColumnAlias.wrapForInnerJoin(f)), 50)
		val p = new Profiler
		val ret = rows.map(r => obj.construct(r))
		p.lap("assembled from protostorables into storableclasses")
		ret
	}

	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
		val sb: StringBuilder = new StringBuilder
		sb.append("SELECT ")
		sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
		sb.append(" FROM " + obj.entityName)
		sb.append(" WHERE " + obj.primaryKey.getPersistenceFieldName + " = " + id)
		val rows: List[ProtoStorable] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrapForInnerJoin(f)), 6)
		if (rows.length == 1) Some(obj.construct(rows.head))
		else None
	}

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		println("#################################################")
		println("About to get " + ids.length + " instances of " + obj.entityName)
		println("#################################################")
		val MAX_IDS_NO_TEMP_TABLE = 50

		if (ids.isEmpty) List.empty
		else if (ids.length <= MAX_IDS_NO_TEMP_TABLE) {
			val sb: StringBuilder = new StringBuilder
			sb.append("SELECT ")
			sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
			sb.append(" FROM " + obj.entityName)
			sb.append(" WHERE " + obj.primaryKey.getPersistenceFieldName + " in (" + ids.mkString(", ") + ")")
			val rows: List[ProtoStorable] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrapForInnerJoin(f)), fetchSize)
			rows.map(r => obj.construct(r))
		} else {
			// Too many IDs; make a filter table
			getObjectsByIdsWithFilterTable(obj, ids, fetchSize)
		}
	}

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] = {
		// Filter("") means a filter that can't possibly match anything.
		// E.g. if you try to make a int in list filter and pass in an empty list, it will generate a short circuit filter
		// If there are any short circuit filters, don't bother talking to the database
		if (filters.exists(f => f(obj.entityName).preparedSQL == "")) List.empty
		else {
			val sb: StringBuilder = new StringBuilder
			sb.append("SELECT ")
			sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
			sb.append(" FROM " + obj.entityName + " " + obj.entityName)
			var params: List[String] = List.empty
			if (filters.nonEmpty) {
				val overallFilter = Filter.and(filters.map(f => f(obj.entityName)))
				sb.append(" WHERE " + overallFilter.preparedSQL)
				params = overallFilter.params
			}
			val rows: List[ProtoStorable] = getProtoStorablesFromSelect(sb.toString(), params, obj.fieldList.map(f => ColumnAlias.wrapForInnerJoin(f)), fetchSize)
			val p = new Profiler
			val ret = rows.map(r => obj.construct(r))
			p.lap("finished construction")
			ret
		}
	}

	protected def countObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		// Filter("") means a filter that can't possibly match anything.
		// E.g. if you try to make a int in list filter and pass in an empty list, it will generate a short circuit filter
		// If there are any short circuit filters, don't bother talking to the database
		if (filters.exists(f => f(obj.entityName).preparedSQL == "")) 0
		else {
			val sb: StringBuilder = new StringBuilder
			sb.append("SELECT COUNT(*) FROM " + obj.entityName + " " + obj.entityName)
			var params: List[String] = List.empty
			if (filters.nonEmpty) {
				val overallFilter = Filter.and(filters.map(f => f(obj.entityName)))
				sb.append(" WHERE " + overallFilter.preparedSQL)
				params = overallFilter.params
			}
			val sql = sb.toString()
			dbGateway.mainPool.withConnection(c => {
				println("counting objects: ")
				println(sql)
				val preparedStatement = c.prepareStatement(sql)
				val preparedParams = params.map(PreparedString)
				preparedParams.zipWithIndex.foreach(t => t._1.set(preparedStatement)(t._2+1))
				println("Parameterized with " + preparedParams)
				val rs: ResultSet = preparedStatement.executeQuery
				rs.next()
				rs.getInt(1)
			})
		}
	}

	private def getObjectsByIdsWithFilterTable[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		val tableName: String = {
			val now: String = System.currentTimeMillis().toString
			val md5: String = MessageDigest.getInstance("MD5").digest(now.getBytes).map("%02x".format(_)).mkString
			"FILTER_" + md5.substring(0, 10).toUpperCase
		}
		println(" ======   Creating filter table " + tableName + "    =======")
		val p = new Profiler
		dbGateway.tempPool.withConnection(c => {
			val createTableSQL = "CREATE TABLE " + tableName + " (ID Number)"
			c.createStatement().executeUpdate(createTableSQL)
			p.lap("Created table")
			println("about to do " + ids.length + " ids....")

			val ps = c.prepareStatement("INSERT INTO " + tableName + " VALUES (?)")
			ids.distinct.foreach(i => {
				ps.setInt(1, i)
				ps.addBatch()
				ps.clearParameters()
			})
			ps.executeBatch()
			p.lap("inserted ids")

			val indexName = tableName + "_IDX1"

			val createIndexSQL = "CREATE UNIQUE INDEX " + indexName + " on " + tableName + " (\"ID\") "
			c.createStatement().executeUpdate(createIndexSQL)
			p.lap("created index")

			val grantSQL = "GRANT INDEX,SELECT ON \"" + tableName + "\" to " + dbGateway.mainUserName
			c.createStatement().executeUpdate(grantSQL)
			p.lap("created Grant")

			val sb: StringBuilder = new StringBuilder
			val ms = dbGateway.mainSchemaName
			val tts = dbGateway.tempSchemaName
			sb.append("SELECT ")
			sb.append(obj.fieldList.map(f => ms + "." + obj.entityName + "." + f.getPersistenceFieldName).mkString(", "))
			sb.append(" FROM " + ms + "." + obj.entityName + ", " + tts + "." + tableName)
			sb.append(" WHERE " + ms + "." + obj.entityName + "." + obj.primaryKey.getPersistenceFieldName + " = " + tts + "." + tableName + ".ID")
			val rows: List[ProtoStorable] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrapForInnerJoin(f)), fetchSize)

			val dropTableSQL = "DROP TABLE " + tableName + " CASCADE CONSTRAINTS"
			c.createStatement().executeUpdate(dropTableSQL)

			println(" =======   cleaned up filter table   =======")
			val p2 = new Profiler
			val ret = rows.map(r => obj.construct(r))
			p2.lap("finished construction")
			ret
		})
	}

	private def executeSQLForInsert(
		sql: String,
		pkPersistenceName: Option[String],
		useTempConnection: Boolean = false,
		params: Option[List[PreparedValue]] = None,
		batchParams: Option[List[List[PreparedValue]]] = None,
	): Option[String] = {
		println(sql.replace("\t", "\\t"))
		val pool = if (useTempConnection) dbGateway.tempPool else dbGateway.mainPool
		pool.withConnection(c => {
			if (batchParams.isDefined && pkPersistenceName.isDefined) {
				throw new Exception("Do not use PK return with batch insert, it doesn't work")
			}
			val ps: PreparedStatement = pkPersistenceName match {
				case Some(s) => c.prepareStatement(sql, scala.Array(s))
				case None => c.prepareStatement(sql)
			}

			if (batchParams.isDefined) {
				if (batchParams.get.length > 100) throw new Exception ("Aborting before inserting over 100 records in batch; implement batch pagination")
				batchParams.get.foreach(row => {
					row.zipWithIndex.foreach(t => t._1.set(ps)(t._2+1))
					println("Parameterized with " + row)
					ps.addBatch()
				})
				ps.executeBatch()
				// Cant return a PK when there are multiple.  Could someday extend this to return a list of PKs
				None
			} else {
				if (params.isDefined) {
					params.get.zipWithIndex.foreach(t => t._1.set(ps)(t._2+1))
					println("Parameterized with " + params.get)
				}
				ps.executeUpdate()
				if (pkPersistenceName.isDefined) {
					val rs = ps.getGeneratedKeys
					if (rs.next) {
						Some(rs.getString(1))
					} else throw new Exception("No pk value came back from insert statement")
				} else None
			}
		})
	}

	private def executeSQLForUpdateOrDelete(sql: String, useTempConnection: Boolean = false, params: Option[List[PreparedValue]] = None): Int = {
		println(sql)
		val pool = if (useTempConnection) dbGateway.tempPool else dbGateway.mainPool
		pool.withConnection(c => {
			println("executing prepared update/delete:")
			println(sql)
			val ps = c.prepareStatement(sql)
			if (params.isDefined) {
				params.get.zipWithIndex.foreach(t => t._1.set(ps)(t._2+1))
				println("Parameterized with " + params.get)
			}
			ps.executeUpdate()
		})
	}

	// This has to be parameterized, otherwise the compiler shits itself.  At this point shouldnt be anything but ColumnAlias
	private def getProtoStorablesFromSelect(sql: String, params: List[String], properties: List[ColumnAlias[_]], fetchSize: Int): List[ProtoStorable] = {
		println(sql)
		val profiler = new Profiler
		dbGateway.mainPool.withConnection(c => {
			val preparedStatement = c.prepareStatement(sql)
			val preparedParams = params.map(PreparedString)
			preparedParams.zipWithIndex.foreach(t => t._1.set(preparedStatement)(t._2+1))
			println("Parameterized with " + preparedParams)
			val rs: ResultSet = preparedStatement.executeQuery

			rs.setFetchSize(fetchSize)

			val rows: ListBuffer[ProtoStorable] = ListBuffer()
			var rowCounter = 0
			profiler.lap("starting rows")
			while (rs.next) {
				rowCounter += 1
				var intFields: Map[ColumnAlias[_], Option[Int]] = Map()
				var doubleFields: Map[ColumnAlias[_], Option[Double]] = Map()
				var stringFields: Map[ColumnAlias[_], Option[String]] = Map()
				var dateFields: Map[ColumnAlias[_], Option[LocalDate]] = Map()
				var dateTimeFields: Map[ColumnAlias[_], Option[LocalDateTime]] = Map()

				val p = new Profiler

				// I don't understand why the compiler bitches if this cast is not present, but it does
				def makeCompilerHappy: ColumnAlias[_] => ColumnAlias[_ <: DatabaseField[_]] = _.asInstanceOf[ColumnAlias[_ <: DatabaseField[_]]]

				properties.zip(1.to(properties.length + 1)).foreach(Function.tupled((ca: ColumnAlias[_], i: Int) => {
					ca.field match {
						case _: IntDatabaseField | _: NullableIntDatabaseField => {
							intFields += (makeCompilerHappy(ca) -> Some(rs.getInt(i)))
							if (rs.wasNull()) intFields += (makeCompilerHappy(ca) -> None)
						}
						case _: DoubleDatabaseField | _: NullableDoubleDatabaseField => {
							doubleFields += (makeCompilerHappy(ca) -> Some(rs.getDouble(i)))
							if (rs.wasNull()) doubleFields += (makeCompilerHappy(ca) -> None)
						}
						case _: StringDatabaseField | _: NullableStringDatabaseField => {
							stringFields += (makeCompilerHappy(ca) -> Some(rs.getString(i)))
							if (rs.wasNull()) stringFields += (makeCompilerHappy(ca) -> None)
						}
						case _: DateDatabaseField | _: NullableDateDatabaseField => {
							dateFields += (makeCompilerHappy(ca) -> {
								try {
									Some(rs.getDate(i).toLocalDate)
								} catch {
									case _: Throwable => None
								}
							})
							if (rs.wasNull()) dateFields += (makeCompilerHappy(ca) -> None)
						}
						case _: DateTimeDatabaseField => {
							dateTimeFields += (makeCompilerHappy(ca) -> Some(rs.getTimestamp(i).toLocalDateTime))
							if (rs.wasNull()) dateTimeFields += (makeCompilerHappy(ca) -> None)
						}
						case _: BooleanDatabaseField => {
							stringFields += (makeCompilerHappy(ca) -> Some(rs.getString(i)))
							if (rs.wasNull()) stringFields += (makeCompilerHappy(ca) -> None)
						}
						case _ => {
							println(" *********** UNKNOWN COLUMN TYPE FOR COL " + ca)
						}
					}
				}))

				rows += new ProtoStorable(intFields, doubleFields, stringFields, dateFields, dateTimeFields, Map())
			}
			profiler.lap(s"finished rows (rowcount: ${rowCounter})")
			val fetchCount: Int = Math.ceil(rowCounter.toDouble / fetchSize.toDouble).toInt
			if (fetchCount > 2) println(" ***********  QUERY EXECUTED " + fetchCount + " FETCHES!!  Rowcount was " + rowCounter + ":  " + sql)
			rows.toList
		})
	}

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit = {
		if (i.hasID) {
			updateObject(i)
		} else {
			if (i.unsetRequiredFields.nonEmpty) {
				throw new Exception("Attempted to insert new StorableClass instance, but not all fields are set: " + i.unsetRequiredFields.map(_.getPersistenceFieldName).mkString(", "))
			} else {
				insertObject(i)
			}
		}
	}


	private def insertObject(i: StorableClass): Unit = {
		println("inserting woooo")

		def getFieldValues(vm: Map[String, FieldValue[_]]): List[FieldValue[_]] =
			vm.values
					.filter(fv => fv.isSet && fv.getPersistenceFieldName != i.companion.primaryKey.getPersistenceFieldName)
					.toList

		val fieldValues: List[FieldValue[_]] = {
			getFieldValues(i.intValueMap) ++
			getFieldValues(i.nullableIntValueMap) ++
			getFieldValues(i.stringValueMap) ++
			getFieldValues(i.nullableStringValueMap) ++
			getFieldValues(i.dateValueMap) ++
			getFieldValues(i.nullableDateValueMap) ++
			getFieldValues(i.dateTimeValueMap) ++
			getFieldValues(i.booleanValueMap)
		}

		val startingColumns = fieldValues.map(fv => fv.getPersistenceFieldName)
		val startingValues = fieldValues.map(fv => fv.getPersistenceLiteral._1)

		val columns = {
			if (i.desiredPrimaryKey.isInitialized) i.getPrimaryKeyFieldValue.getPersistenceFieldName :: startingColumns
			else startingColumns
		}

		val values = {
			if (i.desiredPrimaryKey.isInitialized) i.desiredPrimaryKey.get :: startingValues
			else startingValues
		}

		val sb = new StringBuilder()
		sb.append("INSERT INTO " + i.companion.entityName + " ( ")
		sb.append(columns.mkString(", "))
		sb.append(") VALUES (")
		sb.append(values.mkString(", "))
		sb.append(")")
		println(sb.toString())
		val params = Some(fieldValues.flatMap(fv => fv.getPersistenceLiteral._2).map(PreparedString))
		executeSQLForInsert(sb.toString(), Some(i.companion.primaryKey.getPersistenceFieldName), false, params) match {
			case Some(s: String) => i.initializePrimaryKeyValue(s.toInt)
			case None =>
		}
	}

	private def updateObject(i: StorableClass): Unit = {
		def getFieldValues(vm: Map[String, FieldValue[_]]): List[FieldValue[_]] =
			vm.values
				.filter(fv =>
					fv.isSet &&
					fv.getPersistenceFieldName != i.companion.primaryKey.getPersistenceFieldName &&
					fv.isDirty
				)
				.toList

		val fieldValues: List[FieldValue[_]] =
			getFieldValues(i.intValueMap) ++
					getFieldValues(i.nullableIntValueMap) ++
					getFieldValues(i.stringValueMap) ++
					getFieldValues(i.nullableStringValueMap) ++
					getFieldValues(i.dateValueMap) ++
					getFieldValues(i.nullableDateValueMap) ++
					getFieldValues(i.dateTimeValueMap) ++
					getFieldValues(i.booleanValueMap)

		val sb = new StringBuilder()
		sb.append("UPDATE " + i.companion.entityName + " SET ")
		sb.append(fieldValues.map(fv => fv.getPersistenceFieldName + " = " + fv.getPersistenceLiteral._1).mkString(", "))
		sb.append(" WHERE " + i.companion.primaryKey.getPersistenceFieldName + " = " + i.getID)
		val params = Some(fieldValues.flatMap(fv => fv.getPersistenceLiteral._2).map(PreparedString))
		val updated = executeSQLForUpdateOrDelete(sb.toString(), false, params)
		if (updated != 1) {
			throw new Exception("Attempted to update storable " + i.companion.entityName + ":" + i.getID + ", updated " + updated + " records")
		}
	}

	protected def executeQueryBuilderImplementation(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		val intValues: Map[ColumnAliasInnerJoined[_], Option[Int]] = Map()
		val stringValues: Map[ColumnAliasInnerJoined[_], Option[String]] = Map()

		qb.tables.map(_._1.obj).foreach(_.init())

		val tablesReverse = qb.tables.reverse
		val joinsReverse = qb.joins.reverse

		val mainTable = tablesReverse.head
		val joinTables = tablesReverse.tail
		if (joinTables.length != joinsReverse.length) {
			throw new Exception("Malformed Query Builder, tables and joins dont line up")
		}

		val fields = if (qb.fields.isEmpty) {
			qb.allFields
		} else qb.fields

		var params: List[String] = List.empty
		val joinClause = {
			def recurse(tables: List[(TableAlias[_ <: StorableObject[_ <: StorableClass]], Boolean)], joins: List[TableJoin], clause: String): String = {
				if (tables.isEmpty) clause
				else {
					val table = tables.head
					val join = joins.head
					params = params ::: join.on.params
					val joinKeyword = if (table._2) "LEFT OUTER JOIN" else "INNER JOIN"
					val newClause = clause +
						s"""
						  | $joinKeyword ${table._1.obj.entityName} ${table._1.name}
						  |ON ${join.on.preparedSQL}
						  |""".stripMargin
					recurse(tables.tail, joins.tail, newClause)
				}
			}
			recurse(joinTables, joinsReverse, "")
		}
		val whereFilter = Filter.and(qb.where)
		val whereClause = if(qb.where.nonEmpty) "WHERE " + whereFilter.preparedSQL else ""
		params = params ::: whereFilter.params
		val sql =
			s"""
			  |select ${fields.map(f => f.table.name + "." + f.field.asInstanceOf[DatabaseField[_]].getPersistenceFieldName).mkString(", ")}
			  |from ${mainTable._1.obj.entityName} ${mainTable._1.name}
			  |$joinClause
			  |$whereClause
			  |""".stripMargin

		println("QueryBuilder SQL: " + sql)

		getProtoStorablesFromSelect(sql, params, fields, 500).map(ps => new QueryBuilderResultRow(ps))
	}

	 protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T = {
		val pool = if (pc.useTempSchema) dbGateway.tempPool else dbGateway.mainPool
		pool.withConnection(conn => {
			println("STARTING PROCEDURE CALL: " + pc.getQuery)
			val callable: CallableStatement = conn.prepareCall(s"{call ${pc.getQuery}}")

			// register outs and inouts
			pc.registerOutParameters.foreach(Function.tupled((paramName: String, dataType: Int) => {
				callable.registerOutParameter(paramName, dataType)
			}))

			pc.setInParametersInt.foreach(Function.tupled((paramName: String, value: Int) => {
				println(s"$paramName = $value")
				callable.setInt(paramName, value)
			}))

			pc.setInParametersVarchar.foreach(Function.tupled((paramName: String, value: String) => {
				println(s"$paramName = $value")
				callable.setString(paramName, value)
			}))

			pc.setInParametersDouble.foreach(Function.tupled((paramName: String, value: Double) => {
				println(s"$paramName = $value")
				callable.setDouble(paramName, value)
			}))

			// Date params DO NOT WORK
			// Everything will appear to work and then it will act as though the transaction was not committed
			// Send strings instead and cast to date oracle-side

//			pc.setInParametersDateTime.foreach(Function.tupled((paramName: String, value: LocalDateTime) => {
//				if (value == null) {
//					println(s"$paramName = $value")
//					callable.setDate(paramName, null)
//				} else {
//					println("not null!")
//					val utilDate: java.util.Date = java.util.Date.from(DateUtil.toBostonTime(value).toInstant)
//					println(utilDate.getTime)
//					val sqlDate: java.sql.Date = new java.sql.Date(utilDate.getTime)
//					println(s"$paramName = $sqlDate")
//					callable.setDate(paramName, sqlDate)
//				}
//
//			}))

			val hadResults: Boolean = callable.execute()
			conn.commit()

			pc.getOutResults(callable)
		})
	}

	// TODO: do these belong here or in util somewhere?
	private def dateToLocalDate(d: Date): LocalDate =
		d.toInstant.atZone(ZoneId.systemDefault).toLocalDate

	private def dateToLocalDateTime(d: Date): LocalDateTime =
		d.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime

}

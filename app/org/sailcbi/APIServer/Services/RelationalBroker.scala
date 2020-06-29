package org.sailcbi.APIServer.Services

import java.security.MessageDigest
import java.sql._
import java.time.{LocalDate, LocalDateTime, ZoneId}

import org.sailcbi.APIServer.CbiUtil.Profiler
import org.sailcbi.APIServer.IO.PreparedQueries._
import org.sailcbi.APIServer.Storable.Fields.FieldValue.FieldValue
import org.sailcbi.APIServer.Storable.Fields.{NullableDateDatabaseField, NullableIntDatabaseField, NullableStringDatabaseField, _}
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, QueryBuilder, QueryBuilderResultRow, TableAlias, TableJoin}
import org.sailcbi.APIServer.Storable._

import scala.collection.mutable.ListBuffer

abstract class RelationalBroker private[Services](dbConnection: DatabaseConnection, rc: RequestCache, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends PersistenceBroker(dbConnection, rc, preparedQueriesOnly, readOnly)
{
	//implicit val pb: PersistenceBroker = this

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		val pool = if (pq.useTempSchema) {
			println("using temp schema")
			dbConnection.tempPool
		} else {
			println("using main schema")
			dbConnection.mainPool
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
		case p: PreparedQueryForInsert => executeSQLForInsert(p.getQuery, p.pkName, p.useTempSchema, Some(p.asInstanceOf[PreparedQueryForInsert].getParams))
		case hq: HardcodedQueryForInsert => executeSQLForInsert(hq.getQuery, hq.pkName, hq.useTempSchema)
	}

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int = {
		pq match {
			case p: PreparedQueryForUpdateOrDelete => {
				val pool = if (pq.useTempSchema) dbConnection.tempPool else dbConnection.mainPool
				pool.withConnection(c => {
					println("executing prepared update/delete:")
					println(p.getQuery)
					val preparedStatement = c.prepareStatement(p.getQuery)

					p.getParams.zipWithIndex.foreach(t => t._1.set(preparedStatement)(t._2+1))
					println("Parameterized with " + p.getParams)
//					(p.params.indices zip p.params).foreach(t => {
//						println(s"setting index ${t._1 + 1} to ${t._2}")
//						preparedStatement.setString(t._1 + 1, t._2)
//					})
//					println("Parameterized with " + p.params)
					preparedStatement.executeUpdate()
				})
			}
			case _ => {
				println("executing non-prepared update/delete:")
				executeSQLForUpdateOrDelete(pq.getQuery, pq.useTempSchema)
			}
		}
	}

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T]): List[T] = {
		val sb: StringBuilder = new StringBuilder
		sb.append("SELECT ")
		sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
		sb.append(" FROM " + obj.entityName)
		val rows: List[ProtoStorable[String]] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrap(f)), 50, _.field.asInstanceOf[DatabaseField[_]].getRuntimeFieldName)
		rows.map(r => obj.construct(r, rc))
	}

	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
		val sb: StringBuilder = new StringBuilder
		sb.append("SELECT ")
		sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
		sb.append(" FROM " + obj.entityName)
		sb.append(" WHERE " + obj.primaryKey.getPersistenceFieldName + " = " + id)
		val rows: List[ProtoStorable[String]] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrap(f)), 6, _.field.asInstanceOf[DatabaseField[_]].getRuntimeFieldName)
		if (rows.length == 1) Some(obj.construct(rows.head, rc))
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
			val rows: List[ProtoStorable[String]] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrap(f)), fetchSize, _.field.asInstanceOf[DatabaseField[_]].getRuntimeFieldName)
			rows.map(r => obj.construct(r, rc))
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
			val rows: List[ProtoStorable[String]] = getProtoStorablesFromSelect(sb.toString(), params, obj.fieldList.map(f => ColumnAlias.wrap(f)), fetchSize, _.field.asInstanceOf[DatabaseField[_]].getRuntimeFieldName)
			val p = new Profiler
			val ret = rows.map(r => obj.construct(r, rc))
			p.lap("finished construction")
			ret
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
		dbConnection.tempPool.withConnection(c => {
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

			val grantSQL = "GRANT INDEX,SELECT ON \"" + tableName + "\" to " + dbConnection.mainUserName
			c.createStatement().executeUpdate(grantSQL)
			p.lap("created Grant")

			val sb: StringBuilder = new StringBuilder
			val ms = dbConnection.mainSchemaName
			val tts = dbConnection.tempSchemaName
			sb.append("SELECT ")
			sb.append(obj.fieldList.map(f => ms + "." + obj.entityName + "." + f.getPersistenceFieldName).mkString(", "))
			sb.append(" FROM " + ms + "." + obj.entityName + ", " + tts + "." + tableName)
			sb.append(" WHERE " + ms + "." + obj.entityName + "." + obj.primaryKey.getPersistenceFieldName + " = " + tts + "." + tableName + ".ID")
			val rows: List[ProtoStorable[String]] = getProtoStorablesFromSelect(sb.toString(), List.empty, obj.fieldList.map(f => ColumnAlias.wrap(f)), fetchSize, _.field.asInstanceOf[DatabaseField[_]].getRuntimeFieldName)

			val dropTableSQL = "DROP TABLE " + tableName + " CASCADE CONSTRAINTS"
			c.createStatement().executeUpdate(dropTableSQL)

			println(" =======   cleaned up filter table   =======")
			val p2 = new Profiler
			val ret = rows.map(r => obj.construct(r, rc))
			p2.lap("finished construction")
			ret
		})
	}

	private def executeSQLForInsert(sql: String, pkPersistenceName: Option[String], useTempConnection: Boolean = false, params: Option[List[PreparedValue]] = None): Option[String] = {
		println(sql.replace("\t", "\\t"))
		val pool = if (useTempConnection) dbConnection.tempPool else dbConnection.mainPool
		pool.withConnection(c => {
			val ps: PreparedStatement = pkPersistenceName match {
				case Some(s) => c.prepareStatement(sql, scala.Array(s))
				case None => c.prepareStatement(sql)
			}

			if (params.isDefined) {
				params.get.zipWithIndex.foreach(t => t._1.set(ps)(t._2+1))
				println("Parameterized with " + params.get)
//				(params.get.indices zip params.get).foreach(t => {
//					ps.setString(t._1 + 1, t._2)
//				})
//				println("Parameterized with " + params)
			}
			ps.executeUpdate()
			if (pkPersistenceName.isDefined) {
				val rs = ps.getGeneratedKeys
				if (rs.next) {
					Some(rs.getString(1))
				} else throw new Exception("No pk value came back from insert statement")
			} else None
		})
	}

	private def executeSQLForUpdateOrDelete(sql: String, useTempConnection: Boolean = false): Int = {
		println(sql)
		val pool = if (useTempConnection) dbConnection.tempPool else dbConnection.mainPool
		pool.withConnection(c => {
			val st: Statement = c.createStatement()
			st.executeUpdate(sql) // returns # of rows updated
		})
	}

	private def getProtoStorablesFromSelect[T](sql: String, params: List[String], properties: List[ColumnAlias[_, _]], fetchSize: Int, getKey: ColumnAlias[_, _] => T): List[ProtoStorable[T]] = {
		println(sql)
		val profiler = new Profiler
		dbConnection.mainPool.withConnection(c => {
			val preparedStatement = c.prepareStatement(sql)
			val preparedParams = params.map(PreparedString)
			preparedParams.zipWithIndex.foreach(t => t._1.set(preparedStatement)(t._2+1))
			println("Parameterized with " + preparedParams)
			val rs: ResultSet = preparedStatement.executeQuery

			rs.setFetchSize(fetchSize)

			val rows: ListBuffer[ProtoStorable[T]] = ListBuffer()
			var rowCounter = 0
			profiler.lap("starting rows")
			while (rs.next) {
				rowCounter += 1
				var intFields: Map[T, Option[Int]] = Map()
				var doubleFields: Map[T, Option[Double]] = Map()
				var stringFields: Map[T, Option[String]] = Map()
				var dateFields: Map[T, Option[LocalDate]] = Map()
				var dateTimeFields: Map[T, Option[LocalDateTime]] = Map()


				properties.zip(1.to(properties.length + 1)).foreach(Function.tupled((ca: ColumnAlias[_, _], i: Int) => {
					ca.field match {
						case _: IntDatabaseField | _: NullableIntDatabaseField => {
							intFields += (getKey(ca) -> Some(rs.getInt(i)))
							if (rs.wasNull()) intFields += (getKey(ca) -> None)
						}
						case _: DoubleDatabaseField | _: NullableDoubleDatabaseField => {
							doubleFields += (getKey(ca) -> Some(rs.getDouble(i)))
							if (rs.wasNull()) doubleFields += (getKey(ca) -> None)
						}
						case _: StringDatabaseField | _: NullableStringDatabaseField => {
							stringFields += (getKey(ca) -> Some(rs.getString(i)))
							if (rs.wasNull()) stringFields += (getKey(ca) -> None)
						}
						case _: DateDatabaseField | _: NullableDateDatabaseField => {
							dateFields += (getKey(ca) -> {
								try {
									Some(rs.getDate(i).toLocalDate)
								} catch {
									case _: Throwable => None
								}
							})
							if (rs.wasNull()) dateFields += (getKey(ca) -> None)
						}
						case _: DateTimeDatabaseField => {
							dateTimeFields += (getKey(ca) -> Some(rs.getTimestamp(i).toLocalDateTime))
							if (rs.wasNull()) dateTimeFields += (getKey(ca) -> None)
						}
						case _: BooleanDatabaseField => {
							stringFields += (getKey(ca) -> Some(rs.getString(i)))
							if (rs.wasNull()) stringFields += (getKey(ca) -> None)
						}
						case _ => {
							println(" *********** UNKNOWN COLUMN TYPE FOR COL " + ca)
						}
					}
				}))

				rows += ProtoStorable(intFields, doubleFields, stringFields, dateFields, dateTimeFields, Map())
			}
			profiler.lap(s"finished rows (rowcount: ${rowCounter})")
			val fetchCount: Int = Math.ceil(rowCounter.toDouble / fetchSize.toDouble).toInt
			if (fetchCount > 2) println(" ***********  QUERY EXECUTED " + fetchCount + " FETCHES!!  Rowcount was " + rowCounter + ":  " + sql)
			rows.toList
		})
	}

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit = {
		if (i.hasID) updateObject(i) else insertObject(i)
	}


	private def insertObject(i: StorableClass): Unit = {
		println("inserting woooo")

		def getFieldValues(vm: Map[String, FieldValue[_]]): List[FieldValue[_]] =
			vm.values
					.filter(fv => fv.isSet && fv.getPersistenceFieldName != i.getCompanion.primaryKey.getPersistenceFieldName)
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
		sb.append("INSERT INTO " + i.getCompanion.entityName + " ( ")
		sb.append(fieldValues.map(fv => fv.getPersistenceFieldName).mkString(", "))
		sb.append(") VALUES (")
		sb.append(fieldValues.map(fv => fv.getPersistenceLiteral).mkString(", "))
		sb.append(")")
		println(sb.toString())
		executeSQLForInsert(sb.toString(), Some(i.getCompanion.primaryKey.getPersistenceFieldName))
	}

	private def updateObject(i: StorableClass): Unit = {
		def getUpdateExpressions(vm: Map[String, FieldValue[_]]): List[String] =
			vm.values
					.filter(fv => fv.isSet && fv.getPersistenceFieldName != i.getCompanion.primaryKey.getPersistenceFieldName)
					.map(fv => fv.getPersistenceFieldName + " = " + fv.getPersistenceLiteral)
					.toList

		val updateExpressions: List[String] =
			getUpdateExpressions(i.intValueMap) ++
					getUpdateExpressions(i.nullableIntValueMap) ++
					getUpdateExpressions(i.stringValueMap) ++
					getUpdateExpressions(i.nullableStringValueMap) ++
					getUpdateExpressions(i.dateValueMap) ++
					getUpdateExpressions(i.nullableDateValueMap) ++
					getUpdateExpressions(i.dateTimeValueMap) ++
					getUpdateExpressions(i.booleanValueMap)

		val sb = new StringBuilder()
		sb.append("UPDATE " + i.getCompanion.entityName + " SET ")
		sb.append(updateExpressions.mkString(", "))
		sb.append(" WHERE " + i.getCompanion.primaryKey.getPersistenceFieldName + " = " + i.getID)
		executeSQLForUpdateOrDelete(sb.toString())
	}

	protected def executeQueryBuilderImplementation(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		val intValues: Map[ColumnAlias[_, _], Option[Int]] = Map()
		val stringValues: Map[ColumnAlias[_, _], Option[String]] = Map()

		qb.tables.map(_.obj).foreach(_.init())

		val tablesReverse = qb.tables.reverse
		val joinsReverse = qb.joins.reverse

		val mainTable = tablesReverse.head
		val joinTables = tablesReverse.tail
		if (joinTables.length != joinsReverse.length) {
			throw new Exception("Malformed Query Builder, tables and joins dont line up")
		}
		if (qb.fields.isEmpty) {
			throw new Exception("Malformed Query Builder, no fields specified")
		}
		var params: List[String] = List.empty
		val joinClause = {
			def recurse(tables: List[TableAlias], joins: List[TableJoin], clause: String): String = {
				if (tables.isEmpty) clause
				else {
					val table = tables.head
					val join = joins.head
					params = params ::: join.on.params
					val newClause = clause +
						s"""
						  |${join.joinType.joinText} ${table.obj.entityName} ${table.name}
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
			  |select ${qb.fields.map(f => f.table.name + "." + f.field.asInstanceOf[DatabaseField[_]].getPersistenceFieldName).mkString(", ")}
			  |from ${mainTable.obj.entityName} ${mainTable.name}
			  |$joinClause
			  |$whereClause
			  |""".stripMargin

		println("QueryBuilder SQL: " + sql)

		getProtoStorablesFromSelect(sql, params, qb.fields, 500, ca => ca).map(ps => new QueryBuilderResultRow(ps))
	}

	 protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T = {
		val pool = if (pc.useTempSchema) dbConnection.tempPool else dbConnection.mainPool
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

	// test query
	def testDB {
		dbConnection.mainPool.withConnection(c => {
			val st: Statement = c.createStatement()
			st.execute("select count(*) from users")
			println("test query executed successfully")
		})
	}
}
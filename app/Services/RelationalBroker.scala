package Services

import java.sql._
import java.time.{LocalDate, LocalDateTime, ZoneId}

import CbiUtil.Profiler
import Storable.Fields.FieldValue.FieldValue
import Storable.Fields._
import Storable._
import com.mchange.v2.c3p0.ComboPooledDataSource
import play.api.inject.ApplicationLifecycle

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class RelationalBroker(lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor) extends PersistenceBroker {
  RelationalBroker.createPool(cp, () => {
    lifecycle.addStopHook(() => Future.successful({
      println("  ************    Shutting down!  Closing pool!!  *************  ")
      RelationalBroker.closePool
    }))
  })

  private val pool = RelationalBroker.getPool

  def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
    val sb: StringBuilder = new StringBuilder
    sb.append("SELECT ")
    sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
    sb.append(" FROM " + obj.entityName)
    sb.append(" WHERE " + obj.primaryKeyPersistenceName + " = " + id)
    val rows: List[ProtoStorable] = executeSQL(sb.toString(), obj.fieldList, 6)
    if (rows.length == 1) Some(obj.construct(rows.head, true))
    else None
  }

  def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
    if (ids.isEmpty) List.empty
    else {
      val sb: StringBuilder = new StringBuilder
      sb.append("SELECT ")
      sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
      sb.append(" FROM " + obj.entityName)
      sb.append(" WHERE " + obj.primaryKeyPersistenceName + " in (" + ids.mkString(", ") + ")")
      val rows: List[ProtoStorable] = executeSQL(sb.toString(), obj.fieldList, fetchSize)
      rows.map(r => obj.construct(r, true))
    }
  }

  def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T] = {
    // Filter("") means a filter that can't possibly match anything.
    // E.g. if you try to make a int in list filter and pass in an empty list, it will generate a short circuit filter
    // If there are any short circuit filters, don't bother talking to the database
    if (filters.exists(f => f.sqlString == "")) List.empty
    else {
      val sb: StringBuilder = new StringBuilder
      sb.append("SELECT ")
      sb.append(obj.fieldList.map(f => f.getPersistenceFieldName).mkString(", "))
      sb.append(" FROM " + obj.entityName)
      if (filters.nonEmpty) {
        sb.append(" WHERE " + filters.map(f => f.sqlString).mkString(" AND "))
      }
      val rows: List[ProtoStorable] = executeSQL(sb.toString(), obj.fieldList, fetchSize)
      rows.map(r => obj.construct(r, true))
    }
  }

  def executeSQL(sql: String, properties: List[DatabaseField[_]], fetchSize: Int): List[ProtoStorable] = {
    println(sql)
    val profiler = new Profiler
    val c: Connection = pool.getConnection
    profiler.lap("got connection")
    try {
      val st: Statement = c.createStatement()
      val rs: ResultSet = st.executeQuery(sql)
      rs.setFetchSize(fetchSize)
      val rsmd: ResultSetMetaData = rs.getMetaData


      val rows: ListBuffer[ProtoStorable] = ListBuffer()
      var rowCounter = 0
      profiler.lap("starting rows")
      while (rs.next) {
        rowCounter += 1
        var intFields: Map[String, Option[Int]] = Map()
        var doubleFields: Map[String, Option[Double]] = Map()
        var stringFields: Map[String, Option[String]] = Map()
        var dateFields: Map[String, Option[LocalDate]] = Map()
        var dateTimeFields: Map[String, Option[LocalDateTime]] = Map()


        properties.zip(1.to(properties.length + 1)).foreach(Function.tupled((df: DatabaseField[_], i: Int) => {
          df match {
            case _: IntDatabaseField => {
              intFields += (df.getRuntimeFieldName -> Some(rs.getInt(i)))
              if (rs.wasNull()) intFields += (df.getRuntimeFieldName -> None)
            }
            case _: NullableIntDatabaseField => {
              intFields += (df.getRuntimeFieldName -> Some(rs.getInt(i)))
              if (rs.wasNull()) intFields += (df.getRuntimeFieldName -> None)
            }
            case _: StringDatabaseField => {
              stringFields += (df.getRuntimeFieldName -> Some(rs.getString(i)))
              if (rs.wasNull()) stringFields += (df.getRuntimeFieldName -> None)
            }
            case _: NullableStringDatabaseField => {
              stringFields += (df.getRuntimeFieldName -> Some(rs.getString(i)))
              if (rs.wasNull()) stringFields += (df.getRuntimeFieldName -> None)
            }
            case _: DoubleDatabaseField => {
              doubleFields += (df.getRuntimeFieldName -> Some(rs.getDouble(i)))
              if (rs.wasNull()) doubleFields += (df.getRuntimeFieldName -> None)
            }
            case _: DateTimeDatabaseField => {
              dateTimeFields += (df.getRuntimeFieldName -> Some(rs.getTimestamp(i).toLocalDateTime))
              if (rs.wasNull()) dateTimeFields += (df.getRuntimeFieldName -> None)
            }
            case _: DateDatabaseField => {
              dateFields += (df.getRuntimeFieldName -> Some(rs.getDate(i).toLocalDate))
              if (rs.wasNull()) dateFields += (df.getRuntimeFieldName -> None)
            }
            case _: BooleanDatabaseField => {
              stringFields += (df.getRuntimeFieldName -> Some(rs.getString(i)))
              if (rs.wasNull()) stringFields += (df.getRuntimeFieldName -> None)
            }
            case _ => {
              println(" *********** UNKNOWN COLUMN TYPE FOR COL " + df.getPersistenceFieldName)
            }
          }
        }))

        rows += ProtoStorable(intFields, doubleFields, stringFields, dateFields, dateTimeFields, Map())
      }
      profiler.lap("finsihed rows")
      val fetchCount: Int = Math.ceil(rowCounter.toDouble / fetchSize.toDouble).toInt
      if (fetchCount > 2) println(" ***********  QUERY EXECUTED " + fetchCount + " FETCHES!!  Rowcount was " + rowCounter + ":  " + sql)
      rows.toList
    } finally {
      profiler.lap("about to close")
      c.close()
      profiler.lap("closed")
    }
  }

  def commitObjectToDatabase(i: StorableClass): Unit = {
    if (i.hasID) updateObject(i) else insertObject(i)
  }


  private def insertObject(i: StorableClass): Unit = {
    println("inserting woooo")
  }

  private def updateObject(i: StorableClass): Unit = {
    println("lets update some shiz")
    implicit val pbClass: Class[_ <: PersistenceBroker] = this.getClass
    def getUpdateExpressions(vm: Map[String, FieldValue[_]]) =
      vm.values
        .filter(fv => fv.isSet && fv.getPersistenceFieldName != i.getCompanion.primaryKeyPersistenceName)
        .map(fv => fv.getPersistenceFieldName + " = " + fv.getPersistenceLiteral)
        .toList

    val updateExpressions: List[String] =
      getUpdateExpressions(i.intValueMap) ++
      getUpdateExpressions(i.nullableIntValueMap) ++
      getUpdateExpressions(i.stringValueMap) ++
      getUpdateExpressions(i.nullableStringValueMap) ++
      getUpdateExpressions(i.booleanValueMap) ++
      getUpdateExpressions(i.dateValueMap) ++
      getUpdateExpressions(i.dateTimeValueMap)

    var sb = new StringBuilder()
    sb.append("UPDATE " + i.getCompanion.entityName + " SET ")
    sb.append(updateExpressions.mkString(", "))
    sb.append(" WHERE " + i.getCompanion.primaryKeyPersistenceName + " = " + i.getID)
    println(sb.toString())

  }

  private def dateToLocalDate(d: Date): LocalDate =
    d.toInstant.atZone(ZoneId.systemDefault).toLocalDate

  private def dateToLocalDateTime(d: Date): LocalDateTime =
    d.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime
}

object RelationalBroker {
  var pool: Option[ComboPooledDataSource] = None

  def createPool(cp: ConnectionPoolConstructor, shutdownCallback: (() => Unit)): Unit = {
    println("trying to create a pool...")
    pool match {
      case None => pool = {
        println("...going for it!")
        shutdownCallback()
        Some(cp.getPool)
      }
      case Some(_) => println("...NOOPing that shit")
    }
  }

  def getPool: ComboPooledDataSource = pool match {
    case Some(p) => p
    case None => throw new Exception("Tried to get db connection pool but it wasn't instantiated yet.")
  }

  def closePool(): Unit = pool match {
    case Some(p) => p.close()
    case None =>
  }
}
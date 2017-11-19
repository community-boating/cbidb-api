package Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.Fields.{DateDatabaseField, NullableDateDatabaseField}
import Storable.StorableClass

class NullableDateFieldValue(instance: StorableClass, field: NullableDateDatabaseField) extends FieldValue[Option[LocalDate]](instance, field) {
  def getPersistenceLiteral(implicit pb: PersistenceBroker): String = {
    val d = super.get
    d match {
      case None => "NULL"
      case Some(d) => pb match {
        case _: MysqlBroker => "'" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
        case _: OracleBroker => "TO_DATE('" + d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
      }
    }
  }
}
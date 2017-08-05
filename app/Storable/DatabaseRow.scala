package Storable

import java.time.{LocalDate, LocalDateTime}

import scala.collection.mutable.HashMap

case class DatabaseRow(
  intFields: HashMap[String, Option[Int]],
  doubleFields: HashMap[String, Option[Double]],
  stringFields: HashMap[String, Option[String]],
  dateFields: HashMap[String, Option[LocalDate]],
  dateTimeFields: HashMap[String, Option[LocalDateTime]]
)
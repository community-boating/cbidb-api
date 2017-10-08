package Storable

import java.time.{LocalDate, LocalDateTime}

import scala.collection.mutable.HashMap

// All the data necessary to construct an instance of a storable, in a bunch of hashtables keyed by the property names in the storable
// Storables are almost always instantiated from one of these things as an intermediary,
// whether the data came from the database or a POST request (to go to the database) etc
case class ProtoStorable(
  intFields: HashMap[String, Option[Int]],
  doubleFields: HashMap[String, Option[Double]],
  stringFields: HashMap[String, Option[String]],
  dateFields: HashMap[String, Option[LocalDate]],
  dateTimeFields: HashMap[String, Option[LocalDateTime]]
)
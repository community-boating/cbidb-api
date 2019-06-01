package Storable

import java.time.{LocalDate, LocalDateTime}

import Storable.Fields._

// All the data necessary to construct an instance of a storable, in a bunch of hashtables keyed by the property names in the storable
// Storables are almost always instantiated from one of these things as an intermediary,
// whether the data came from the database or a POST request (to go to the database) etc
// If it came from a POST or something, ignored is the post parameters that were not able to be matched to a single field (for debugging)
case class ProtoStorable(
								intFields: Map[String, Option[Int]],
								doubleFields: Map[String, Option[Double]],
								stringFields: Map[String, Option[String]],
								dateFields: Map[String, Option[LocalDate]],
								dateTimeFields: Map[String, Option[LocalDateTime]],
								ignored: Map[String, String]
						)

object ProtoStorable {
	def constructFromStrings[T <: StorableClass](storable: StorableObject[T], formData: Map[String, String]): ProtoStorable = {
		var intFields: Map[String, Option[Int]] = Map()
		var doubleFields: Map[String, Option[Double]] = Map()
		var stringFields: Map[String, Option[String]] = Map()
		var dateFields: Map[String, Option[LocalDate]] = Map()
		var dateTimeFields: Map[String, Option[LocalDateTime]] = Map()
		var ignored: Map[String, String] = Map()

		formData.foreach(Function.tupled((key: String, value: String) => {
			val intFieldMatch: Option[IntDatabaseField] = storable.intFieldMap.get(key)
			val nullableIntFieldMatch: Option[NullableIntDatabaseField] = storable.nullableIntFieldMap.get(key)
			val doubleFieldMatch: Option[DoubleDatabaseField] = storable.doubleFieldMap.get(key)
			val nullableDoubleFieldMatch: Option[NullableDoubleDatabaseField] = storable.nullableDoubleFieldMap.get(key)
			val stringFieldMatch: Option[StringDatabaseField] = storable.stringFieldMap.get(key)
			val nullableStringFieldMatch: Option[NullableStringDatabaseField] = storable.nullableStringFieldMap.get(key)
			val dateFieldMatch: Option[DateDatabaseField] = storable.dateFieldMap.get(key)
			val nullableDateFieldMatch: Option[NullableDateDatabaseField] = storable.nullableDateFieldMap.get(key)
			val dateTimeFieldMatch: Option[DateTimeDatabaseField] = storable.dateTimeFieldMap.get(key)
			val booleanFieldMatch: Option[BooleanDatabaseField] = storable.booleanFieldMap.get(key)

			def wasMatch(o: Option[_]): Int = o match {
				case Some(_) => 1;
				case None => 0;
			}

			val matches: List[Option[_]] = List(
				intFieldMatch,
				nullableIntFieldMatch,
				doubleFieldMatch,
				nullableDoubleFieldMatch,
				stringFieldMatch,
				nullableStringFieldMatch,
				booleanFieldMatch,
				dateFieldMatch,
				dateTimeFieldMatch
			)

			val numberMatches: Int = matches.map(wasMatch).sum

			// If the field name doesnt match exactly 1 field of this storable, dump the param into ignored for potential debugging
			if (numberMatches != 1) ignored += (key -> value)
			else {
				intFieldMatch match {
					case Some(f: IntDatabaseField) => f.getValueFromString(value) match {
						case Some(i: Int) => intFields += (key -> Some(i))
						case None => ignored += (key -> value)
					}
					case None =>
				}
				nullableIntFieldMatch match {
					case Some(f: NullableIntDatabaseField) => f.getValueFromString(value) match {
						case Some(Some(i: Int)) => intFields += (key -> Some(i))
						case Some(None) => intFields += (key -> None)
						case None => ignored += (key -> value)
					}
					case None =>
				}
				doubleFieldMatch match {
					case Some(f: DoubleDatabaseField) => f.getValueFromString(value) match {
						case Some(i: Double) => doubleFields += (key -> Some(i))
						case None => ignored += (key -> value)
					}
					case None =>
				}
				nullableDoubleFieldMatch match {
					case Some(f: NullableDoubleDatabaseField) => f.getValueFromString(value) match {
						case Some(Some(i: Double)) => doubleFields += (key -> Some(i))
						case Some(None) => doubleFields += (key -> None)
						case None => ignored += (key -> value)
					}
					case None =>
				}
				stringFieldMatch match {
					case Some(f: StringDatabaseField) => stringFields += (key -> Some(value))
					case None =>
				}
				nullableStringFieldMatch match {
					case Some(f: NullableStringDatabaseField) => f.getValueFromString(value) match {
						case Some(Some(s)) => stringFields += (key -> Some(s))
						case Some(None) => stringFields += (key -> None)
						case None => ignored += (key -> value)
					}
					case None =>
				}
				dateFieldMatch match {
					case Some(f: DateDatabaseField) => f.getValueFromString(value) match {
						case Some(d: LocalDate) => dateFields += (key -> Some(d))
						case None => ignored += (key -> value)
					}
					case None =>
				}
				nullableDateFieldMatch match {
					case Some(f: NullableDateDatabaseField) => f.getValueFromString(value) match {
						case Some(Some(d: LocalDate)) => dateFields += (key -> Some(d))
						case Some(None) => dateFields += (key -> None)
						case None => ignored += (key -> value)
					}
					case None =>
				}
				dateTimeFieldMatch match {
					case Some(f: DateTimeDatabaseField) => f.getValueFromString(value) match {
						case Some(dt: LocalDateTime) => dateTimeFields += (key -> Some(dt))
						case None => ignored += (key -> value)
					}
					case None =>
				}
			}
		}))

		ProtoStorable(intFields, doubleFields, stringFields, dateFields, dateTimeFields, ignored)
	}
}
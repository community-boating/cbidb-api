package com.coleji.neptune.Storable

import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, TableAlias}

import java.time.{LocalDate, LocalDateTime}

// All the data necessary to construct an instance of a storable, in a bunch of hashtables keyed by the property names in the storable
// Storables are almost always instantiated from one of these things as an intermediary,
// whether the data came from the database or a POST request (to go to the database) etc
// If it came from a POST or something, ignored is the post parameters that were not able to be matched to a single field (for debugging)
class ProtoStorable(
	val intFields: Map[ColumnAlias[_], Option[Int]],
	val doubleFields: Map[ColumnAlias[_], Option[Double]],
	val stringFields: Map[ColumnAlias[_], Option[String]],
	val dateFields: Map[ColumnAlias[_], Option[LocalDate]],
	val dateTimeFields: Map[ColumnAlias[_], Option[LocalDateTime]],
	val ignored: Map[ColumnAlias[_], String]
) {
	def populateStorable(storable: StorableClass, tableAlias: TableAlias[_]): Unit = {

	}

	override def toString = s"ProtoStorable(intFields=$intFields\n\n doubleFields=$doubleFields\n\n stringFields=$stringFields\n\n dateFields=$dateFields\n\n dateTimeFields=$dateTimeFields\n\n ignored=$ignored)"
}

package Storable

import Services.RequestCache
import Storable.Fields.DatabaseField

case class EntityVisibility(
	entityVisible: Boolean, // false if the user can't do shit with this table
	instanceFilter: Option[(RequestCache => Set[Int])], // Function that specifies which IDs the user can pull.
	// If None, they can pull anything
	fieldList: Option[Set[DatabaseField[_]]] // Set of fields the user can get data from.  If None, no restriction
)

object EntityVisibility {
	// Entity is fully queryable
	val FULL_VISIBILITY = EntityVisibility(entityVisible = true, None, None)

	// Entity with this visibility is not queryable
	val ZERO_VISIBILITY = EntityVisibility(entityVisible = false, Some((rc: RequestCache) => Set.empty), Some(Set.empty))

	// Can get all rows, but cannot get any data point on them
	val ROW_COUNT_VISIBILITY = EntityVisibility(entityVisible = true, None, Some(Set.empty))
}

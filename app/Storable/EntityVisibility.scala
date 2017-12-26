package Storable

import Services.RequestCache
import Storable.Fields.DatabaseField

case class EntityVisibility(
  entityVisible: Boolean,                             // false if the user can't do shit with this table
  instanceFilter: Option[(RequestCache => Set[Int])], // Function that specifies which IDs the user can pull.
                                                      // If None, they can pull anything
  fieldList: Option[Set[DatabaseField[_]]]            // Set of fields the user can get data from.  If None, no restriction
)

// Entity is fully queryable
case object FULL_VISIBILITY extends EntityVisibility(true, None, None)

// Entity with this visibility is not queryable
case object ZERO_VISIBILITY extends EntityVisibility(false, Some((rc: RequestCache) => Set.empty), Some(Set.empty))

// Can get all rows, but cannot get any data point on them
case object ROW_COUNT_VISIBILITY extends EntityVisibility(true, None, Some(Set.empty))
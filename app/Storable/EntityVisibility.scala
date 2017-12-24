package Storable

import Services.RequestCache

sealed abstract class EntityVisibility

// Entity with this visibility is not queryable
case object VISIBLE_ALWAYS extends EntityVisibility

// Entity is always queryable
case object VISIBLE_NEVER extends EntityVisibility

// Entity is queryable, but an "id in list" filter will always be added
case class VISIBLE_SOME_IDS(
  f: (RequestCache => Set[Int])
) extends EntityVisibility
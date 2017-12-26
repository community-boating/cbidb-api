package Entities

import Storable.{EntityVisibility, FULL_VISIBILITY, StorableClass, StorableObject}
import Entities._

object EntitySecurity {
  val publicSecurity: Map[StorableObject[_ <: StorableClass], EntityVisibility] = Map(
    ApClassFormat -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassInstance -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassSession -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassSignup -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassType -> EntityVisibility(entityVisible=true, None, Some(Set.empty))
  )
}

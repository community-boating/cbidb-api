package Entities

import Entities.EntityDefinitions._
import Storable.{EntityVisibility, StorableClass, StorableObject}

object EntitySecurity {
  val publicSecurity: Map[StorableObject[_ <: StorableClass], EntityVisibility] = Map(
    ApClassFormat -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassInstance -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassSession -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassSignup -> EntityVisibility(entityVisible=true, None, Some(Set.empty)),
    ApClassType -> EntityVisibility(entityVisible=true, None, Some(Set.empty))
  )
}

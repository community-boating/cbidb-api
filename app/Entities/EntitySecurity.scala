package Entities

import Entities.EntityDefinitions._
import Storable.{EntityVisibility, StorableClass, StorableObject}

object EntitySecurity {
  val publicSecurity: Map[StorableObject[_ <: StorableClass], EntityVisibility] = Map(
    ApClassFormat -> EntityVisibility(entityVisible=true, None, Some(Set(
      ApClassFormat.fields.typeId
    ))),
    ApClassInstance -> EntityVisibility(entityVisible=true, None, Some(Set(
      ApClassInstance.fields.formatId,
      ApClassInstance.fields.locationString
    ))),
    ApClassSession -> EntityVisibility(entityVisible=true, None, Some(Set(
      ApClassSession.fields.instanceId,
      ApClassSession.fields.sessionDateTime
    ))),
    ApClassSignup -> EntityVisibility(entityVisible=true, None, Some(Set(
      ApClassSignup.fields.instanceId
    ))),
    ApClassType -> EntityVisibility(entityVisible=true, None, Some(Set(
      ApClassType.fields.displayOrder,
      ApClassType.fields.typeName
    ))),
    ClassInstructor -> EntityVisibility(entityVisible=true, None, Some(Set(
      ClassInstructor.fields.nameFirst,
      ClassInstructor.fields.nameLast
    ))),
    ClassLocation -> EntityVisibility(entityVisible=true, None, Some(Set(
      ClassLocation.fields.locationName
    ))),
    JpClassInstance -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpClassInstance.fields.locationId,
      JpClassInstance.fields.typeId,
      JpClassInstance.fields.instructorId
    ))),
    JpClassSession -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpClassSession.fields.instanceId,
      JpClassSession.fields.sessionDateTime
    ))),
    JpClassSignup -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpClassSignup.fields.instanceId
    ))),
    JpClassType -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpClassType.fields.displayOrder,
      JpClassType.fields.typeName
    ))),
    JpTeam -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpTeam.fields.teamName
    ))),
    JpTeamEventPoints -> EntityVisibility(entityVisible=true, None, Some(Set(
      JpTeamEventPoints.fields.teamId,
      JpTeamEventPoints.fields.points
    )))
  )
}

package Reporting

import Entities.{ApClassInstance, JpClassInstance}
import Storable.StorableObject

class Report {

}

object Report {
  val PERMITTED_BASE_ENTITIES: Set[StorableObject[_]] = Set (
    JpClassInstance,
    ApClassInstance
  )
}
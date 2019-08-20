package Storable.StorableQuery

import Storable.{StorableClass, StorableObject}

case class TableAlias(name: String, obj: StorableObject[_ <: StorableClass])

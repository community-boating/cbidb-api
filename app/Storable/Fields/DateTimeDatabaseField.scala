package Storable.Fields

import Storable.StorableObject

class DateTimeDatabaseField(entity: StorableObject[_], fieldName: String) extends DateDatabaseField(entity, fieldName)
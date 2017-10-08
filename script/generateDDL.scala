import java.io.File

import Entities._
import Services.{MysqlBroker, PersistenceBroker}
import Storable.Fields.FieldValue.FieldValue
import Storable.{StorableClass, StorableObject}

////////////////////////////////////
implicit val pbClass: Class[_ <: PersistenceBroker] = classOf[MysqlBroker]
////////////////////////////////////

type Entity = StorableObject[_ <: StorableClass]

val entities: List[Entity] = List(
  ApClassFormat,
  ApClassInstance,
  ApClassSession,
  ApClassSignup,
  ApClassType,
  ClassInstructor,
  ClassLocation,
  JpClassInstance,
  JpClassSession,
  JpClassSignup,
  JpClassType,
  JpTeam,
  JpTeamEventPoints,
  User
)

// start: validate all entities are present
val entityNamesFromFiles: Set[String] = {
  val path ="app/Entities"
  val d = new File(path)
  if (d.exists && d.isDirectory) {
    d.listFiles.filter(_.isFile).map(f => {
     val name = f.getName
      name.substring(0, name.length - 6)
    }).toSet
  } else {
    throw new Exception("No entities found, probably bad directory")
  }
}


val entityNamesFromClasses: Set[String] = entities.map(e => {
  val rawName = e.getClass.getName
  rawName.substring(9, rawName.length-1)
}).toSet

if (!entityNamesFromFiles.equals(entityNamesFromClasses)) throw new Exception("Entities are missing")

// end : validate

// start: export ddl

val dropTables: String = entities.map("drop table " + _.entityName + ";").mkString("\n")

val createTables: String = entities.map(e => {
  "create table " + e.entityName + "(" + e.fieldList.map(f => {
    f.getPersistenceFieldName + " " + f.getFieldType
  }).mkString(", ") + ");"
}).mkString("\n")

val getSeedData: (Entity => List[StorableClass]) = e => e.getSeedData.toList
val seedObjects: List[StorableClass] = entities.flatMap(getSeedData)

val inserts: String = seedObjects.map(obj => {
  val fieldValues: List[FieldValue] = obj.deconstruct.toList
  val sb = new StringBuilder
  sb.append("INSERT INTO " + obj.companion.entityName + "(")
  sb.append(fieldValues.map(_.getPersistenceFieldName).mkString(", "))
  sb.append(") values (")
  sb.append(fieldValues.map(_.getPersistenceLiteral).mkString(", "))
  sb.append(");")

  sb.toString()
}).mkString("\n")

println(dropTables + "\n\n" + createTables + "\n\n" + inserts)


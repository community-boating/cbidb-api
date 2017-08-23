import java.io.File

import Entities._
import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.StorableObject
import oracle.net.aso.e

////////////////////////////////////
val pbClass: Class[_ <: PersistenceBroker] = classOf[MysqlBroker]
////////////////////////////////////

type Entity = StorableObject[_]

val entities: Set[Entity] = Set(
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
  JpTeamEventPoints
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
})

if (!entityNamesFromFiles.equals(entityNamesFromClasses)) throw new Exception("Entities are missing")

// end : validate

// start: export ddl

val dropTables: String = entities.map("drop table " + _.entityName + ";").mkString("\n")

val createTables: String = entities.map(e => {
  "create table " + e.entityName + "(" + e.fieldList.map(f => {
    f.getFieldName + " " + f.getFieldType(pbClass)
  }).mkString(", ") + ");"
}).mkString("\n")

val inserts: Set[_] = entities.flatMap(e => e.getTestData).map()
println(dropTables + "\n\n" + createTables)


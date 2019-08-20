package ScalaTest.Database

import Entities.EntityDefinitions.{JpClassInstance, JpClassType}
import ScalaTest.GetPersistenceBroker
import Storable.StorableQuery.{ColumnAlias, Query, TableAlias}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DBTest extends FunSuite{
//	test("Test db connection") {
//		GetPersistenceBroker { pb =>
//			val firstClass = pb.getObjectById(JpClassType, 1)
//			val name = (firstClass.orNull.values.typeName.get)
//			assert(name == ("Learn to Sail"))
//		}
//	}

	test("Test query") {
		val typeId = ColumnAlias(TableAlias("types", JpClassType), JpClassType.fields.typeId)
		val typeName = ColumnAlias(TableAlias("types", JpClassType), JpClassType.fields.typeName)

		val q = new Query(List(
			typeId,
			typeName
		), List.empty)

		val id = q.getValue(typeId)
		val name = q.getValue(typeName)

		assert(id == 4)
		assert(name == "b")
	}
}

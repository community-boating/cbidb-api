package ScalaTest.Database

import Entities.EntityDefinitions.{JpClassInstance, JpClassType}
import ScalaTest.GetPersistenceBroker
import Storable.Query
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
		val q = Query(JpClassType)(f => List(f.typeName))

		assert(q.doThing == List(JpClassType.fields.typeName))
	}
}

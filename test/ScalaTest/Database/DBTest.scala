package ScalaTest.Database

import Entities.EntityDefinitions.{JpClassInstance, JpClassType}
import ScalaTest.GetPersistenceBroker
import Storable.StorableQuery.{ColumnAlias, JoinPoint, QueryBuilder, TableAlias}
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
		val types = TableAlias("types", JpClassType)
		val instances = TableAlias("i", JpClassInstance)

		val types_typeId = ColumnAlias(types, JpClassType.fields.typeId)
		val typeName = ColumnAlias(types, JpClassType.fields.typeName)

		val instances_typeId = ColumnAlias(instances, JpClassInstance.fields.typeId)
		val instanceId = ColumnAlias(instances, JpClassInstance.fields.instanceId)

		val q = new QueryBuilder(List(
			types_typeId,
			typeName,
			instanceId
		), List(
			JoinPoint(types_typeId, instances_typeId)
		), List(
			types_typeId.filter(_.equalsConstant(1))
		))

		GetPersistenceBroker { pb =>
			pb.executeQueryBuilder(q)
		}

	}
}

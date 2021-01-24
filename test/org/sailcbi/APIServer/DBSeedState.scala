package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Services.Authentication.RootRequestCache
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DBSeedState extends FunSuite {
//
//
//	test("nuke") {
//		ServerBootLoaderTest.withPAWriteable(pa => {
//			val p = new Profiler()
//			pa.nukeDB()
//		})
//	}


//	test("reflection") {
//		ServerBootLoaderTest.withPA(pa => {
//			val rc = pa.assertRC(AuthenticationInstance.ROOT)
//
//			val instances = pb.getAllObjectsOfClass(JpClassInstance)
//			instances.foreach(_.booleanValueMap)
//		})
//	}

	test("concurrent update") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val seedState = List(
				new User()
					.update(_.active, true)
					.update(_.nameFirst, Some("Bob"))
					.update(_.nameLast, Some("Smith"))
					.update(_.userName, "bsmith")
					.update(_.email, "bsmith@sdfg.com")
					.withPK(9998)
			)
			val rc = pa.assertRC(RootRequestCache.create)

			pa.withSeedState(seedState, () => {
				val users = rc.getAllObjectsOfClass(User, Some(List(User.fields.userId, User.fields.nameFirst, User.fields.nameLast, User.fields.active)))
				val user = users.head
				println("Seed user has id " + user.getID)
				val startingValue = user.values.nameLast.get
				user.values.nameLast.initialize(Some(user.values.nameLast.get.get + "!"))
				rc.commitObjectToDatabase(user)

				val usersAgain = rc.getAllObjectsOfClass(User, Some(List(User.fields.userId, User.fields.nameFirst, User.fields.nameLast, User.fields.active)))
				val userAgain = usersAgain.head
				val endingValue = userAgain.values.nameLast.get
				assert(startingValue.contains("Smith") && endingValue.contains("Smith!"))
			})
		})
	}

	test("check all entities has valuesList") {
		ServerBootLoaderTest.withPA(pa => {
			pa.instantiateAllEntityCompanions()
			assert(pa.checkAllEntitiesHaveValuesList.isEmpty)
		})
	}

	test("check all valuesLists match reflection") {
		ServerBootLoaderTest.withPA(pa => {
			pa.instantiateAllEntityCompanions()
			assert(pa.checkAllValueListsMatchReflection.isEmpty)
		})
	}
}

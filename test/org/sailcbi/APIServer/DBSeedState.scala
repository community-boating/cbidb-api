package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.CbiUtil.Profiler
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, JpClassInstance, MembershipType, User}
import org.sailcbi.APIServer.Services.Authentication.AuthenticationInstance
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.reflect.runtime.universe

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
//			val pb = rc.pb
//			val instances = pb.getAllObjectsOfClass(JpClassInstance)
//			instances.foreach(_.booleanValueMap)
//		})
//	}

	test("concurrent update") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val seedState = List(
				new User()
					.set(_.active, true)
					.set(_.nameFirst, Some("Bob"))
					.set(_.nameLast, Some("Smith"))
					.set(_.userName, "bsmith")
					.set(_.email, "bsmith@sdfg.com")
					.withPK(9998)
			)
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb
			pa.withSeedState(seedState, () => {
				val users = pb.getAllObjectsOfClass(User, Some(List(User.fields.userId, User.fields.nameFirst, User.fields.nameLast, User.fields.active)))
				val user = users.head
				println("Seed user has id " + user.getID)
				val startingValue = user.values.nameLast.get
				user.values.nameLast.set(Some(user.values.nameLast.get.get + "!"))
				pb.commitObjectToDatabase(user)

				val usersAgain = pb.getAllObjectsOfClass(User, Some(List(User.fields.userId, User.fields.nameFirst, User.fields.nameLast, User.fields.active)))
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
package org.sailcbi.APIServer.IO.Junior

import java.time.LocalDateTime

import org.sailcbi.APIServer.CbiUtil.{DateUtil, DefinedInitializableNullary}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.{PersistenceBroker, ResultSetWrapper}

object JPPortal {
	def persistProtoParent(pb: PersistenceBroker, cookieValue: String): Int = {
		val pq = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(cookieValue)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				s"""
				  |insert into persons (temp, protoperson_cookie, proto_state, person_type) values ('P', ?, 'I', ${MagicIds.PERSON_TYPE.JP_PARENT})
				  |""".stripMargin
		}
		pb.executePreparedQueryForInsert(pq).get.toInt
	}

	def persistProtoJunior(pb: PersistenceBroker, parentPersonId: Int, firstName: String): (Int, () => Unit) = {
		val createJunior = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(firstName)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				"""
				  |insert into persons (temp, name_first, proto_state) values ('P', ?, 'I')
				  |""".stripMargin
		}
		val juniorPersonId = pb.executePreparedQueryForInsert(createJunior).get.toInt

		val createAcctLink = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(
				parentPersonId.toString,
				juniorPersonId.toString,
				MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK.toString
			)
			override val pkName: Option[String] = None

			override def getQuery: String =
				"""
				  |insert into person_relationships(a,b,type_id) values (?, ?, ?)
				  |""".stripMargin
		}
		pb.executePreparedQueryForInsert(createAcctLink)

		val rollback = () => {
			val deletePersonRelationship = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from person_relationships where b = ?
					  |""".stripMargin
			}
			val deletePerson = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from persons where person_id = ?
					  |""".stripMargin
			}

			pb.executePreparedQueryForUpdateOrDelete(deletePersonRelationship)
			val deletedCt = pb.executePreparedQueryForUpdateOrDelete(deletePerson)
		}
		(juniorPersonId, rollback)
	}

	def deleteProtoJunior(pb: PersistenceBroker, parentPersonId: Int, firstName: String): Either[String, String] = {
		val matchingIdsQuery = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(parentPersonId.toString, firstName)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select p.person_id from persons p, person_relationships rl
				  |where p.person_id = rl.b and rl.a = ?
				  |and p.name_first = ?
				  |and proto_state = '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}'
				  |""".stripMargin
		}

		val ids = pb.executePreparedQueryForSelect(matchingIdsQuery)
		if (ids.nonEmpty) {
			val deleteSignups = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from jp_class_signups where person_id in (${ids.mkString(", ")})
					  |""".stripMargin
			}
			val deletePersonRelationship = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from person_relationships where b in (${ids.mkString(", ")})
					  |""".stripMargin
			}
			val deletePerson = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from persons where person_id in (${ids.mkString(", ")})
					  |""".stripMargin
			}

			pb.executePreparedQueryForUpdateOrDelete(deleteSignups)
			pb.executePreparedQueryForUpdateOrDelete(deletePersonRelationship)
			pb.executePreparedQueryForUpdateOrDelete(deletePerson)
			Right(ids.mkString(", "))
		} else {
			Left("Unable to locate distinct junior to delete")
		}
	}

	def getMinSignupTimeForParent(pb: PersistenceBroker, parentPersonId: Int): Option[LocalDateTime] = {
		val q = new PreparedQueryForSelect[Option[LocalDateTime]](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(parentPersonId.toString)

			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Option[LocalDateTime] = rs.getOptionLocalDateTime(1)

			override def getQuery: String =
				"""
				  |select min(signup_datetime) from jp_class_signups si, person_relationships rl
				  |where si.person_id = rl.b and rl.a = ? and si.signup_type = 'P'
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.flatten
	}

	def spotsLeft(pb: PersistenceBroker, instanceId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.spots_left(?) from dual
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.getOrElse(0)
	}

	def seeTypeFromInstanceId(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_type(?, i.type_id) from jp_class_instances i where i.instance_id = ?
				  |""".stripMargin
		}
		val queryResult = pb.executePreparedQueryForSelect(q).headOption
		val ret = queryResult.getOrElse('N') == "Y"
		println("seeTypeFromInstanceId: queryResult: " + queryResult + "; returning " + ret)
		ret
	}

	def seeInstance(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_instance(?, ?) from dual
				  |""".stripMargin
		}
		val queryResult = pb.executePreparedQueryForSelect(q).headOption
		val ret = queryResult.getOrElse('N') == "Y"
		println("seeInstance: queryResult: " + queryResult + "; returning " + ret)
		ret
	}

	def allowEnroll(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Option[String] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.allow_enroll(?, ?) from dual
				  |""".stripMargin
		}
		val ret = pb.executePreparedQueryForSelect(q).headOption.flatten
		println("allow enroll result: " + ret)
		ret
	}

	// either an error message or a rollback function
	def attemptSingleClassSignup(pb: PersistenceBroker, juniorPersonId: Int, instanceId: Int, signupDatetime: Option[LocalDateTime]): Either[String, () => Unit] = {
		lazy val hasSpots = {
			val spots = JPPortal.spotsLeft(pb, instanceId)
			if (spots > 0) Right(spots)
			else Left("The class has filled.")
		}

		lazy val seeType = if(JPPortal.seeTypeFromInstanceId(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class type.")
		lazy val seeInstance = if(JPPortal.seeInstance(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class.")
		lazy val allowEnroll = JPPortal.allowEnroll(pb, juniorPersonId, instanceId) match {
			case None => Right()
			case Some(err) => Left(err)
		}

		val validationResult = for {
			_ <- hasSpots
			_ <- seeType
			_ <- seeInstance
			x <- allowEnroll
		} yield x

		validationResult.map(_ => {
			println("inserting signup for " + instanceId)
			val q = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(
					instanceId.toString,
					juniorPersonId.toString,
					"P",
					(signupDatetime match {
						case Some(dt) => signupDatetime.get
						case None => LocalDateTime.now()
					}).format(DateUtil.DATE_TIME_FORMATTER)
				)
				override val pkName: Option[String] = Some("SIGNUP_ID")

				override def getQuery: String =
					s"""
					  |insert into jp_class_signups(instance_id, person_id, signup_type, signup_datetime)
					  |values (?, ?, ? , to_date(?, '${DateUtil.DATE_TIME_FORMAT_SQL}'))
					  |""".stripMargin
			}
			val signupId = pb.executePreparedQueryForInsert(q)
			println("inserted, got back signup id " + signupId)
			// return a rollback fn
			() => {
				println("Rolling back " + signupId.get)
				val rollbackQ = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
					override val params: List[String] = List(signupId.get)

					override def getQuery: String =
						"""
						  |delete from jp_class_signups where signup_id = ?
						  |""".stripMargin
				}
				pb.executePreparedQueryForUpdateOrDelete(rollbackQ)
			}
		})
	}

	// Return Some(error message) on fail, None on success
	def attemptSignup(
		pb: PersistenceBroker,
		juniorPersonId: Int,
		beginnerInstanceId: Option[Int],
		intermediateInstanceId: Option[Int],
		signupDatetime: Option[LocalDateTime]
	): Option[String] = {
		// Make each of these initializables; they will only be initialized if the elements before them in the for comp were successes
		// If it's not initialized then we know it was never run, and therefore we don't need to run its rollback
		val beginnerResult = new DefinedInitializableNullary(() => beginnerInstanceId match {
			case None => Right(() => {})
			case Some(id) => JPPortal.attemptSingleClassSignup(pb, juniorPersonId, id, signupDatetime)
		})

		val intermediateResult = new DefinedInitializableNullary(() => intermediateInstanceId match {
			case None => Right(() => {})
			case Some(id) => JPPortal.attemptSingleClassSignup(pb, juniorPersonId, id, signupDatetime)
		})

		val totalResult = for {
			_ <- beginnerResult.get()
			x <- intermediateResult.get()
		} yield x

		if (totalResult.isLeft) {
			println("was a fail")
			beginnerResult.forEach({
				case Right(rollback) => {
					println("should be calling the rollback...")
					rollback()
				}
			})
			Some(totalResult.swap.getOrElse(""))
		} else None
	}
}

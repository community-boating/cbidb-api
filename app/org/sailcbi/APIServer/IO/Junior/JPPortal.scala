package org.sailcbi.APIServer.IO.Junior

import java.time.LocalDateTime

import org.sailcbi.APIServer.CbiUtil.Failover
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.{ProtoPersonUserType, PublicUserType}
import org.sailcbi.APIServer.Services.{PersistenceBroker, ResultSetWrapper}

object JPPortal {
	def persistProtoParent(pb: PersistenceBroker, cookieValue: String): Int = {
		val pq = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(cookieValue)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				"""
				  |insert into persons (temp, protoperson_cookie, proto_state) values ('P', ?, 'I')
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
		val q = new PreparedQueryForSelect[Int](Set(PublicUserType)) {
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
		val q = new PreparedQueryForSelect[String](Set(PublicUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_type(?, i.type_id) from jp_class_instances i where i.instance_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.getOrElse('N') == "Y"
	}

	def seeInstance(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(PublicUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_instance(?, ?) from dual
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.getOrElse('N') == "Y"
	}

	def allowEnroll(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[Option[String]](Set(PublicUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.allow_enroll(?, ?) from dual
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.flatten.isEmpty
	}

	def attemptSingleClassSignup(pb: PersistenceBroker, juniorPersonId: Int, instanceId: Int): Option[String] = {
//		val hasSpots = spotsLeft(pb, instanceId) > 0
//		val seeType = seeTypeFromInstanceId(pb, juniorPersonId, instanceId)
//		val seeInstance = seeInstance(pb, juniorPersonId, instanceId)
//		val allowEnroll = allowEnroll(pb, juniorPersonId, instanceId)

		None
	}

	// Return Some(error message) on fail, None on success
	def attemptSignup(pb: PersistenceBroker, juniorPersonId: Int, beginnerInstanceId: Option[Int], intermediateInstanceId: Option[Int]): Option[String] = {
		beginnerInstanceId match {
			case Some(b) => {

			}
			case None =>
		}
		None
	}
}

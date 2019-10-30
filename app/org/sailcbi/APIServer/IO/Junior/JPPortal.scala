package org.sailcbi.APIServer.IO.Junior

import java.sql.CallableStatement
import java.time.LocalDateTime

import org.sailcbi.APIServer.Api.{ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{DateUtil, DefinedInitializableNullary}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Logic.JuniorProgramLogic
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, ProtoPersonUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, ResultSetWrapper}

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

	def alreadyStarted(pb: PersistenceBroker, instanceId: Int): Either[String, Unit] = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select bk.instance_id from jp_class_bookends bk, jp_class_sessions fs
				  |where bk.first_session = fs.session_id and bk.instance_id = ?
				  |and fs.session_datetime > util_pkg.get_sysdate
				  |""".stripMargin
		}
		if (pb.executePreparedQueryForSelect(q).nonEmpty) Right() else Left("That class has already started.")
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
	def attemptSingleClassSignup(pb: PersistenceBroker, juniorPersonId: Int, instanceId: Int, signupDatetime: Option[LocalDateTime])(implicit pa: PermissionsAuthority): Either[String, () => Unit] = {
		lazy val hasSpots = {
			val spots = JPPortal.spotsLeft(pb, instanceId)
			if (spots > 0) Right(spots)
			else Left("The class has no open seats at this time.  Wait listing is available once payment is processed and registration is complete.")
		}

		lazy val alreadyStarted = JPPortal.alreadyStarted(pb, instanceId)

		lazy val seeType = if(JPPortal.seeTypeFromInstanceId(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class type.")
		lazy val seeInstance = if(JPPortal.seeInstance(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class.")
		lazy val allowEnroll = JPPortal.allowEnroll(pb, juniorPersonId, instanceId) match {
			case None => Right()
			case Some(err) => Left(err)
		}

		val validationResult = for {
			_ <- alreadyStarted
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
						case None => pa.now()
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
				case _ =>
			})
			Some(totalResult.swap.getOrElse(""))
		} else None
	}

	def pruneOldReservations(pb: PersistenceBroker): Int = {
		val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
			override val params: List[String] = List.empty

			override def getQuery: String =
				s"""
				  |delete from jp_class_signups
				  |where signup_type = 'P'
				  |and signup_datetime + (${JuniorProgramLogic.SIGNUP_RESERVATION_HOLD_MINUTES} / (24 * 60)) < util_pkg.get_sysdate
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(q)
	}

	def getOrderId(pb: PersistenceBroker, personId: Int): Int = {
		val pc = new PreparedProcedureCall[Int](Set(MemberUserType)) {
//			procedure get_or_create_order_id(
//					i_person_id in number,
//					o_order_id out number
//			) ;
			override def registerOutParameters: Map[String, Int] = Map(
				"o_order_id" -> java.sql.Types.INTEGER
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId
			)


			override def setInParametersVarchar: Map[String, String] = Map.empty
			override def setInParametersDouble: Map[String, Double] = Map.empty
			override def getOutResults(cs: CallableStatement): Int = cs.getInt("o_order_id")
			override def getQuery: String = "cc_pkg.get_or_create_order_id(?, ?)"
		}
		pb.executeProcedure(pc)
	}

	def getCardData(pb: PersistenceBroker, orderId: Int): Option[StripeTokenSavedShape] = {
		val cardDataQ = new PreparedQueryForSelect[StripeTokenSavedShape](Set(MemberUserType)) {
			override val params: List[String] = List(orderId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): StripeTokenSavedShape = new StripeTokenSavedShape(
				token = rsw.getString(2),
				orderId = rsw.getInt(1),
				last4 = rsw.getString(3),
				expMonth = rsw.getInt(4).toString,
				expYear = rsw.getInt(5).toString,
				zip = rsw.getOptionString(6)
			)

			override def getQuery: String =
				"""
				  |select ORDER_ID, TOKEN, CARD_LAST_DIGITS, CARD_EXP_MONTH, CARD_EXP_YEAR, CARD_ZIP
				  |from active_stripe_tokens where order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(cardDataQ).headOption
	}

	def getOrderTotal(pb: PersistenceBroker, orderId: Int): Double = {

		val orderTotalQ = new PreparedQueryForSelect[Double](Set(MemberUserType)) {
			override val params: List[String] = List(orderId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Double = rsw.getDouble(1)

			override def getQuery: String = "select cc_pkg.calculate_total(?) from dual"
		}
		pb.executePreparedQueryForSelect(orderTotalQ).head
	}

	def seeType(pb: PersistenceBroker, juniorId: Int, typeId: Int): ValidationResult = {
		val canSeeType = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select jp_class_pkg.see_type(?, ?) from dual
				   |""".stripMargin

			override val params: List[String] = List(juniorId.toString, typeId.toString)
		}).head
		if (canSeeType) {
			ValidationOk
		} else {
			ValidationResult.from("You are not eligible to take that class.")
		}
	}

	def seeInstance(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		val canSeeInstance = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select jp_class_pkg.see_instance(?, ?) from dual
				   |""".stripMargin

			override val params: List[String] = List(juniorId.toString, instanceId.toString)
		}).head
		if (canSeeInstance) {
			ValidationOk
		} else {
			ValidationResult.from("You are not eligible to take that class.")
		}
	}

	def allowEnroll(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		val allowEnrollError = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Option[String]](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Option[String] = rs.getOptionString(1)

			override def getQuery: String =
				s"""
				   |select jp_class_pkg.allow_enroll(?, ?) from dual
				   |""".stripMargin

			override val params: List[String] = List(juniorId.toString, instanceId.toString)
		}).head

		allowEnrollError match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	def alreadyStarted(pb: PersistenceBroker, instanceId: Int): ValidationResult = {
		val started = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

			override def getQuery: String =
				s"""
				   |select 1 from jp_class_sessions fs, jp_class_bookends bk
				   |where bk.first_session = fs.session_id and bk.instance_id = ?
				   |and fs.session_datetime < util_pkg.get_sysdate
				   |""".stripMargin

			override val params: List[String] = List(instanceId.toString)
		}).nonEmpty

		if (started) {
			ValidationResult.from("That class has already started.")
		} else {
			ValidationOk
		}
	}

	def waitListExists(pb: PersistenceBroker, instanceId: Int): ValidationResult = {
		val wlExists = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select jp_class_pkg.wl_exists(?) from dual
				   |""".stripMargin

			override val params: List[String] = List(instanceId.toString)
		}).head
		if (wlExists) {
			ValidationOk
		} else {
			ValidationResult.from("This class is not fully open yet; waitlisting will become available after the last seats have opened.")
		}
	}

	def canWaitListJoin(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		val canJoin = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

			override def getQuery: String =
				s"""
				   |select
				   |1
				   |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2,
				   |jp_class_bookends bk, jp_weeks w, jp_class_signups si, jp_class_wl_results wlr
				   |where i.type_id = t.type_id and bk.instance_Id = i.instance_id and bk.first_session = s1.session_id and bk.last_session = s2.session_id
				   |and si.signup_id = wlr.signup_id (+)
				   |and si.instance_Id = i.instance_id and si.person_id = ? and trunc(s1.session_datetime) > trunc(util_pkg.get_sysdate)
				   |and s1.session_datetime between w.monday and (w.sunday)
				   |and si.signup_type = 'W'
				   |and wlr.wl_result is not null and wlr.wl_result = 'P'
				   |and i.instance_id = ?
				   |""".stripMargin

			override val params: List[String] = List(juniorId.toString, instanceId.toString)
		}).nonEmpty

		if (canJoin) {
			ValidationOk
		} else {
			ValidationResult.from("There was an error enrolling you into this class. If this message persists please call the Front Office at 617-523-1038.")
		}
	}
}

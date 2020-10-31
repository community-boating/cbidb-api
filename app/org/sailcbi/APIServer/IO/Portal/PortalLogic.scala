package org.sailcbi.APIServer.IO.Portal

import java.sql.CallableStatement
import java.time.{LocalDate, LocalDateTime}

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil, DefinedInitializableNullary, GetSQLLiteralPrepared}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Logic.MembershipLogic
import org.sailcbi.APIServer.Services.Authentication.{BouncerUserType, MemberUserType, ProtoPersonUserType, PublicUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}

object PortalLogic {
	def persistProtoParent(pb: PersistenceBroker, cookieValue: String): Int = {
		val pq = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(cookieValue)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				s"""
				  |insert into persons (temp, protoperson_cookie, proto_state, person_type) values ('P', ?, 'I', ${MagicIds.PERSON_TYPE.JP_PARENT})
				  |""".stripMargin
		}
		val parentId = pb.executePreparedQueryForInsert(pq).get.toInt
		getOrderId(pb, parentId)
		parentId
	}
	def persistProtoJunior(pb: PersistenceBroker, parentPersonId: Int, firstName: String, hasClassReservations: Boolean): (Int, () => Unit) = {
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

		val orderId = getOrderId(pb, parentPersonId)

		val createSCM = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
			override val params: List[String] = List(
				juniorPersonId.toString,
				orderId.toString,
				if (hasClassReservations) "Y" else null
			)
			override val pkName: Option[String] = None

			override def getQuery: String =
				"""
				  |insert into shopping_cart_memberships(
				  |      person_id,
				  |      membership_type_id,
				  |      ready_to_buy,
				  |      order_id,
				  |      price,
				  |      HAS_JP_CLASS_RESERVATIONS
				  |    ) values (
				  |      ?,
				  |      10,
				  |      'N',
				  |      ?,
				  |      (select price from membership_types where membership_type_id = 10),
				  |      ?
				  |    )
				  |""".stripMargin
		}

		pb.executePreparedQueryForInsert(createSCM)

		val rollback = () => {
			val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(juniorPersonId.toString, orderId.toString)

				override def getQuery: String =
					"""
					  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
					  |""".stripMargin
			}
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
			pb.executePreparedQueryForUpdateOrDelete(deleteSCM)
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
			val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from shopping_cart_memberships where person_id in (${ids.mkString(", ")})
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
			pb.executePreparedQueryForUpdateOrDelete(deleteSCM)
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
		val list = pb.executePreparedQueryForSelect(q)
		println(list)
		list.headOption.flatten
	}

	def spotsLeft(pb: PersistenceBroker, instanceId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType, MemberUserType)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.spots_left(?) from dual
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).headOption.getOrElse(0)
	}

	def hasSpotsLeft(pb: PersistenceBroker, instanceId: Int, messageOverride: Option[String]): ValidationResult = {
		if (spotsLeft(pb, instanceId) > 0) ValidationOk
		else ValidationResult.from(messageOverride.getOrElse("This class is full."))
	}

	def alreadyStarted(pb: PersistenceBroker, instanceId: Int): Either[String, Unit] = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType, MemberUserType)) {
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

	def alreadyStartedAsValidationResult(pb: PersistenceBroker, instanceId: Int): ValidationResult = {
		alreadyStarted(pb, instanceId) match {
			case Left(s) => ValidationResult.from(s)
			case Right(_) => ValidationOk
		}
	}

	def seeTypeFromInstanceId(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonUserType, MemberUserType)) {
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

	def seeTypeFromInstanceIdAsValidationResult(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		if (seeTypeFromInstanceId(pb, juniorId, instanceId)) ValidationOk
		else ValidationResult.from("You are not eligible for this class type.")
	}

	def seeInstance(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonUserType, MemberUserType)) {
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

	def seeInstanceAsValidationResult(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		if (seeInstance(pb, juniorId, instanceId)) ValidationOk
		else ValidationResult.from("You are not eligible for this class.")
	}

	def allowEnroll(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Option[String] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(ProtoPersonUserType, MemberUserType)) {
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

	def allowEnrollAsValidationResult(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		allowEnroll(pb, juniorId, instanceId) match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	// either an error message or a rollback function
	def attemptSingleClassSignupReservation(
		pb: PersistenceBroker,
		juniorPersonId: Int,
		instanceId: Int,
		signupDatetime: Option[LocalDateTime],
		orderId: Int
	)(implicit pa: PermissionsAuthority): Either[String, () => Unit] = {
		lazy val hasSpots = {
			val spots = PortalLogic.spotsLeft(pb, instanceId)
			if (spots > 0) Right(spots)
			else Left("The class has no open seats at this time.  Wait listing is available once payment is processed and registration is complete.")
		}

		lazy val alreadyStarted = PortalLogic.alreadyStarted(pb, instanceId)

		lazy val seeType = if(PortalLogic.seeTypeFromInstanceId(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class type.")
		lazy val seeInstance = if(PortalLogic.seeInstance(pb, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class.")
		lazy val allowEnroll = PortalLogic.allowEnroll(pb, juniorPersonId, instanceId) match {
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
			println("datetime is " + signupDatetime)
			val signupId = actuallyEnroll(pb, instanceId, juniorPersonId, signupDatetime, doEnroll = true, fullEnroll = false, Some(orderId))
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

	def actuallyEnroll(
		pb: PersistenceBroker,
		instanceId: Int,
		juniorPersonId: Int,
		signupDatetime: Option[LocalDateTime],
		doEnroll: Boolean,
		fullEnroll: Boolean,
		orderId: Option[Int]
	)(implicit pa: PermissionsAuthority): Option[String] = {
//		procedure signup(
//				p_instance_id in number,
//				p_junior_id in number,
//				p_override in char,
//				p_send_email in char default 'Y',
//				p_signup_datetime in date default null,
//				p_enroll_type in char default 'E',
//				p_do_fallback_wl in char default 'Y',
//				o_signup_id out number
//		)

		val enrollType = {
			if (doEnroll) {
				if (fullEnroll) "E"
				else "P"
			} else "W"
		}

		val proc = new PreparedProcedureCall[Int](Set(ProtoPersonUserType, MemberUserType)) {
			override def registerOutParameters: Map[String, Int] = Map(
				"o_signup_id" -> java.sql.Types.INTEGER
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"p_instance_id" -> instanceId,
				"p_junior_id" -> juniorPersonId,
				"p_order_id" -> orderId.getOrElse(-1)
			)


			override def setInParametersVarchar: Map[String, String] = Map(
				"p_override" -> "N",
				"p_send_email" -> "N",
				"p_enroll_type" -> enrollType,
				"p_do_fallback_wl" -> "N",
				"p_signup_datetime" -> signupDatetime.map(_.format(DateUtil.DATE_TIME_FORMATTER)).orNull
			)

			override def getOutResults(cs: CallableStatement): Int = cs.getInt("o_signup_id")
			override def getQuery: String = "jp_class_pkg.signup_core(?, ?, ?, ?, ?, ?, ?, ?, ?)"
		}

		Some(pb.executeProcedure(proc).toString)
	}

	// Return Some(error message) on fail, None on success
	def attemptSignupReservation(
		pb: PersistenceBroker,
		juniorPersonId: Int,
		beginnerInstanceId: Option[Int],
		intermediateInstanceId: Option[Int],
		signupDatetime: Option[LocalDateTime],
		orderId: Int
	): Option[String] = {
		// Make each of these initializables; they will only be initialized if the elements before them in the for comp were successes
		// If it's not initialized then we know it was never run, and therefore we don't need to run its rollback
		val beginnerResult = new DefinedInitializableNullary(() => beginnerInstanceId match {
			case None => Right(() => {})
			case Some(id) => PortalLogic.attemptSingleClassSignupReservation(pb, juniorPersonId, id, signupDatetime, orderId)
		})

		val intermediateResult = new DefinedInitializableNullary(() => intermediateInstanceId match {
			case None => Right(() => {})
			case Some(id) => PortalLogic.attemptSingleClassSignupReservation(pb, juniorPersonId, id, signupDatetime, orderId)
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
		val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType, MemberUserType)) {
			override val params: List[String] = List.empty

			override def getQuery: String =
				s"""
				  |delete from jp_class_signups
				  |where signup_type = 'P'
				  |and signup_datetime + (jp_class_pkg.get_minutes_to_reserve_class / (24 * 60)) < util_pkg.get_sysdate
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(q)
	}

	def getOrderId(pb: PersistenceBroker, personId: Int): Int = {
		val pc = new PreparedProcedureCall[Int](Set(MemberUserType, ProtoPersonUserType)) {
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

	def canWaitListJoin(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Boolean = {
		pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberUserType)) {
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
	}

	def attemptDeleteSignup(pb: PersistenceBroker, juniorId: Int, instanceId: Int): ValidationResult = {
		val proc = new PreparedProcedureCall[Option[String]](Set(MemberUserType)) {
//			procedure attempt_delete_signup(
//					i_person_id in number,
//					i_instance_id in number,
//					o_error out clob
//			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> juniorId,
				"i_instance_id" -> instanceId
			)

			override def registerOutParameters: Map[String, Int] = Map(
				"o_error" -> java.sql.Types.CLOB
			)

			override def getOutResults(cs: CallableStatement): Option[String] = {
				val ret = cs.getString("o_error")
				if (cs.wasNull()) None
				else Some(ret)
			}

			override def getQuery: String = "jp_class_pkg.attempt_delete_signup(?, ?, ?)"
		}
		pb.executeProcedure(proc) match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	case class SignupForReport(instanceId: Int, typeId: Int, className: String, week: String, dateString: String, timeString: String)
	object SignupForReport {

		implicit val format = Json.format[SignupForReport]

		def apply(v: JsValue): SignupForReport = v.as[SignupForReport]
	}

	def getSignupsForReport(pb: PersistenceBroker, juniorId: Int): List[SignupForReport] = {
		val q = new PreparedQueryForSelect[SignupForReport](Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): SignupForReport = SignupForReport(
				rsw.getInt(1),
				rsw.getInt(2),
				rsw.getString(3),
				rsw.getString(4),
				rsw.getString(5),
				rsw.getString(6)
			)

			override def getQuery: String =
				"""
				  |select
				  |i.instance_id,
				  |t.type_id,
				  |t.type_name as class_name,
				  |(case w.week when 0 then 'Spring' when 11 then 'Fall' else 'Week '||w.week end) as week,
				  |to_char(s1.session_datetime,'Mon ddth')||
				  |(case when s1.session_datetime <> s2.session_datetime then ' - '||to_char(s2.session_datetime,'Mon ddth') end) as class_date,
				  |to_char(s1.session_datetime,'HH:MIPM')||' - '||
				  |to_char(s1.session_datetime+(nvl(s1.length_override,t.session_length)/24),'HH:MIPM')||'  '||
				  |to_char(s1.session_datetime,'Dy')||
				  |(case when s1.session_datetime <> s2.session_datetime then ' - '||
				  |to_char(s2.session_datetime,'Dy') end)  as class_times
				  |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2, jp_class_bookends bk, jp_class_signups si, jp_weeks w
				  |where i.type_id = t.type_id and bk.instance_Id = i.instance_id and bk.first_session = s1.session_id and bk.last_session = s2.session_id
				  |and si.instance_Id = i.instance_id and si.person_id = ? and trunc(s2.session_datetime) >= trunc(util_pkg.get_sysdate)
				  |and s1.session_datetime between w.monday and (w.sunday)
				  |and si.signup_type = 'E'
				  |order by s1.session_datetime
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	case class WaitListTopForReport(
		instanceId: Int,
		typeId: Int,
		className: String,
		week: String,
		dateString: String,
		timeString: String,
		offerExpiresString: String,
		offerExpDatetime: LocalDateTime,
		nowDateTime: LocalDateTime
	)
	object WaitListTopForReport {

		implicit val format = Json.format[WaitListTopForReport]

		def apply(v: JsValue): WaitListTopForReport = v.as[WaitListTopForReport]
	}

	def getWaitListTopsForReport(pb: PersistenceBroker, juniorId: Int): List[WaitListTopForReport] = {
		val q = new PreparedQueryForSelect[WaitListTopForReport](Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): WaitListTopForReport = WaitListTopForReport(
				rsw.getInt(1),
				rsw.getInt(2),
				rsw.getString(3),
				rsw.getString(4),
				rsw.getString(5),
				rsw.getString(6),
				rsw.getString(7),
				rsw.getLocalDateTime(8),
				rsw.getLocalDateTime(9)
			)

			override def getQuery: String =
				"""
				  |select
				  |i.instance_id,
				  |t.type_id,
				  |t.type_name as class_name,
				  |(case w.week when 0 then 'Spring' when 11 then 'Fall' else 'Week '||w.week end) as week,
				  |to_char(s1.session_datetime,'Mon ddth')||
				  |(case when s1.session_datetime <> s2.session_datetime then ' - '||to_char(s2.session_datetime,'Mon ddth') end) as class_date,
				  |to_char(s1.session_datetime,'HH:MIPM')||' - '||
				  |to_char(s1.session_datetime+(nvl(s1.length_override,t.session_length)/24),'HH:MIPM')||'  '||
				  |to_char(s1.session_datetime,'Dy')||
				  |(case when s1.session_datetime <> s2.session_datetime then ' - '||
				  |to_char(s2.session_datetime,'Dy') end)  as class_times,
				  |to_char(offer_exp_datetime,'Month ddth HH:MIPM') as offer_exp,
				  |offer_exp_datetime,
				  |util_pkg.get_sysdate
				  |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2, jp_class_bookends bk, jp_weeks w, jp_class_signups si, jp_class_wl_results wlr
				  |where i.type_id = t.type_id and bk.instance_Id = i.instance_id and bk.first_session = s1.session_id and bk.last_session = s2.session_id
				  |and si.signup_id = wlr.signup_id (+)
				  |and si.instance_Id = i.instance_id and si.person_id = ? and trunc(s1.session_datetime) >= trunc(util_pkg.get_sysdate)
				  |and s1.session_datetime between w.monday and (w.sunday)
				  |and si.signup_type = 'W'
				  |and wlr.wl_result is not null and wlr.wl_result <> 'F'
				  |order by s1.session_datetime
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	case class WaitListForReport(instanceId: Int, typeId: Int, className: String, week: String, dateString: String, timeString: String, wlPosition: Int)
	object WaitListForReport {

		implicit val format = Json.format[WaitListForReport]

		def apply(v: JsValue): WaitListForReport = v.as[WaitListForReport]
	}

	def getWaitListsForReport(pb: PersistenceBroker, juniorId: Int): List[WaitListForReport] = {
		val q = new PreparedQueryForSelect[WaitListForReport](Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): WaitListForReport = WaitListForReport(
				rsw.getInt(1),
				rsw.getInt(2),
				rsw.getString(3),
				rsw.getString(4),
				rsw.getString(5),
				rsw.getString(6),
				rsw.getInt(7)
			)

			override def getQuery: String =
				"""
				  |select
				  |i.instance_id,
				  |t.type_id,
				  |t.type_name as class_name,
				  |(case w.week when 0 then 'Spring' when 11 then 'Fall' else 'Week '||w.week end) as week,
				  |to_char(s1.session_datetime,'Mon ddth')||(case when s1.session_datetime <> s2.session_datetime then ' - '||
				  |to_char(s2.session_datetime,'Mon ddth') end) as class_date,
				  |to_char(s1.session_datetime,'HH:MIPM')||' - '||
				  |to_char(s1.session_datetime+(nvl(s1.length_override,t.session_length)/24),'HH:MIPM')||'  '||
				  |to_char(s1.session_datetime,'Dy')||(case when s1.session_datetime <> s2.session_datetime then ' - '||
				  |to_char(s2.session_datetime,'Dy') end)  as class_times,
				  |(select count(*) from jp_class_signups where instance_id = i.instance_id and signup_type = 'W' and sequence <= si.sequence) as line_length
				  |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2, jp_class_bookends bk, jp_weeks w, jp_class_signups si, jp_class_wl_results wlr
				  |where i.type_id = t.type_id and bk.instance_Id = i.instance_id and bk.first_session = s1.session_id and bk.last_session = s2.session_id
				  |and si.signup_id = wlr.signup_id (+)
				  |and si.instance_Id = i.instance_id and si.person_id = ? and trunc(s1.session_datetime) >= trunc(util_pkg.get_sysdate)
				  |and s1.session_datetime between w.monday and (w.sunday)
				  |and si.signup_type = 'W'
				  |and wlr.wl_result is null
				  |order by s1.session_datetime
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	def addSCMIfNotMember(pb: PersistenceBroker, parentPersonId: Int, juniorId: Int): Unit = {
		val orderId = getOrderId(pb, parentPersonId)
		val getCurrentMembership = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select 1 from active_jp_members where person_id = ?
				  |""".stripMargin
		}
		val hasCurrentMembership = pb.executePreparedQueryForSelect(getCurrentMembership).nonEmpty

		val hasSCMQuery = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override val params: List[String] = List(
				juniorId.toString,
				orderId.toString
			)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select 1 from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		val hasSCM = pb.executePreparedQueryForSelect(hasSCMQuery).nonEmpty
		if (!hasCurrentMembership && !hasSCM) {

			val createSCM = new PreparedQueryForInsert(Set(MemberUserType)) {
				override val params: List[String] = List(
					juniorId.toString,
					orderId.toString
				)
				override val pkName: Option[String] = None

				override def getQuery: String =
					"""
					  |insert into shopping_cart_memberships(
					  |      person_id,
					  |      membership_type_id,
					  |      ready_to_buy,
					  |      order_id,
					  |      price
					  |    ) values (
					  |      ?,
					  |      10,
					  |      'N',
					  |      ?,
					  |      (select price from membership_types where membership_type_id = 10)
					  |    )
					  |""".stripMargin
			}
			pb.executePreparedQueryForInsert(createSCM)
		}
	}

	def deleteRegistration(pb: PersistenceBroker, parentPersonId: Int, juniorId: Int): ValidationResult = {
		val orderId = getOrderId(pb, parentPersonId)
		val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(deleteSCM)

		val deleteClassReservations = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString)

			override def getQuery: String =
				"""
				  |delete from jp_class_signups where person_id = ? and signup_type = 'P'
				  |""".stripMargin
		}

		pb.executePreparedQueryForUpdateOrDelete(deleteClassReservations)
		ValidationOk
	}

	def apDeleteReservation(pb: PersistenceBroker, memberID: Int): ValidationResult = {
		val orderId = getOrderId(pb, memberID)
		val deleteSCGP = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_guest_privs where order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(deleteSCGP)

		val deleteSCDW = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(memberID.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_waivers where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(deleteSCDW)

		val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(memberID.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(deleteSCM)

		ValidationOk
	}

	def canCheckout(pb: PersistenceBroker, parentPersonId: Int, orderId: Int): Boolean = {
		val incompleteItemsQuery = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override val params: List[String] = List(parentPersonId.toString, parentPersonId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select
				  |1
				  |from persons p, shopping_cart_memberships sc
				  |where p.person_id = sc.person_id
				  |and p.person_id in (select b from acct_links where a = ? union select to_number(?) from dual)
				  |and (sc.ready_to_buy = 'N')
				  |
				  |""".stripMargin
		}

		val hasIncompleteItems = pb.executePreparedQueryForSelect(incompleteItemsQuery).nonEmpty

		val completeItemsQuery = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override val params: List[String] = List(
				parentPersonId.toString,
				parentPersonId.toString,
				orderId.toString,
				parentPersonId.toString,
				parentPersonId.toString,
				orderId.toString,
				orderId.toString
			)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select
				  |1
				  |from persons p, shopping_cart_memberships sc
				  |where p.person_id = sc.person_id
				  |and p.person_id in (select b from acct_links where a = ? union select to_number(?) from dual)
				  |and (sc.ready_to_buy = 'Y')
				  |
				  |union all
				  |
				  |select 1 from persons p, shopping_cart_jpc scj
				  |where p.person_id = scj.person_id
				  |and scj.order_id = ?
				  |and p.person_id in (select b from acct_links where a = ? union select to_number(?) from dual)
				  |and (scj.ready_to_buy = 'Y')
				  |
				  |union all
				  |
				  |select 1 from shopping_cart_guest_privs
				  |where real_membership_id is not null and order_id = ?
				  |
				  |union all
				  |
				  |select 1 from shopping_cart_waivers
				  |where order_id = ?
				  |""".stripMargin
		}

		val hasCompleteItems = pb.executePreparedQueryForSelect(completeItemsQuery).nonEmpty

		hasCompleteItems && !hasIncompleteItems
	}

	def getSignupNote(pb: PersistenceBroker, juniorId: Int, instanceId: Int): Either[ValidationError, Option[String]] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(MemberUserType)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override def getQuery: String =
				"""
				  |select PARENT_SIGNUP_NOTE from jp_class_signups
				  |where person_id = ? and instance_id = ?
				  |""".stripMargin
		}
		val result = pb.executePreparedQueryForSelect(q)
		if (result.length == 1) Right(result.head)
		else Left(ValidationResult.from("Unable to locate signup for this class."))
	}

	def saveSignupNote(pb: PersistenceBroker, juniorId: Int, instanceId: Int, signupNote: Option[String]): ValidationResult = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberUserType, ProtoPersonUserType)) {
			override val params: List[String] = List(
				signupNote.orNull,
				juniorId.toString,
				instanceId.toString
			)

			override def getQuery: String =
				"""
				  |update jp_class_signups
				  |set parent_signup_note = ?
				  |where person_id = ? and instance_id = ?
				  |""".stripMargin
		}

		val result = pb.executePreparedQueryForUpdateOrDelete(q)
		if (result == 1) ValidationOk
		else ValidationResult.from("Unable to update signup note.")
	}

	def assessDiscounts(pb: PersistenceBroker, orderId: Int): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(MemberUserType)) {
//			procedure assess_discounts(
//				p_order_id in number
//			);
			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = Unit

			override def setInParametersInt: Map[String, Int] = Map(
				"p_order_id" -> orderId
			)

			override def getQuery: String =
				"""
				  |cc_pkg.assess_discounts(?)
				  |""".stripMargin
		}
		pb.executeProcedure(ppc)
	}

	def removeOffseasonWaitlist(pb: PersistenceBroker, juniorId: Int): Unit = {
		val q = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select signup_id from jp_class_signups si, jp_class_instances i, jp_class_types t,
				  |      jp_class_bookends bk, jp_class_sessions fs, jp_weeks w
				  |      where si.instance_id = i.instance_id and i.type_id = t.type_id
				  |      and si.person_id = ? and si.signup_type = 'W'
				  |      and i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				  |      and fs.session_datetime between w.monday and w.sunday
				  |      and w.week in (0,11,12) and w.season = util_pkg.get_current_season
				  |""".stripMargin

			override val params: List[String] = List(juniorId.toString)
		}

		val ids = pb.executePreparedQueryForSelect(q)

		if (ids.nonEmpty) {
			val deleteSignupsQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(juniorId.toString)
				override def getQuery: String =
					s"""
					   |delete from jp_class_signups where person_id = ? and signup_id in (${ids.mkString(",")})
					   |""".stripMargin
			}

			pb.executePreparedQueryForUpdateOrDelete(deleteSignupsQ)

			val deleteWLQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override def getQuery: String =
					s"""
					   |delete from jp_class_wl_results wl
					   |where wl.signup_id in (${ids.mkString(",")})
					   |""".stripMargin
			}

			pb.executePreparedQueryForUpdateOrDelete(deleteWLQ)
		}
	}

	def getDiscountsWithAmounts(pb: PersistenceBroker): List[DiscountWithAmount] = {
		val q = new PreparedQueryForSelect[DiscountWithAmount](Set(MemberUserType)) {
			override val params: List[String] = List()

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): DiscountWithAmount =
				DiscountWithAmount(
					discountId = rsw.getInt(1),
					instanceId = rsw.getInt(2),
					membershipTypeId = rsw.getInt(3),
					fullPrice = rsw.getDouble(4),
					discountAmount = rsw.getDouble(5)
				)

			override def getQuery: String =
				s"""
					|select
					|dai.discount_id,
					|dai.instance_id,
					|m.membership_type_id,
					|m.price,
					|nvl(md.discount_amt,0) as amt
					|from memberships_discounts md, discount_active_instances dai, membership_types m
					|where md.instance_id = dai.instance_id and m.membership_type_id = md.type_id
					|""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	case class DiscountWithAmount (
		discountId: Int,
		instanceId: Int,
		membershipTypeId: Int,
		fullPrice: Double,
		discountAmount: Double
	)

	def getFYExpirationDate(pb: PersistenceBroker, personId: Int): (Int, LocalDate) = {
		val q = new PreparedQueryForSelect[(Int, LocalDate)](Set(MemberUserType)) {
			override val params: List[String] = List(personId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, LocalDate) =
				(rsw.getInt(1), rsw.getLocalDate(2))

			override def getQuery: String =
				s"""
					|select membership_type_id, expiration_date from persons_memberships where assign_id =
					| (select max(assign_id) from persons_memberships where person_id = ? and void_close_id is null and membership_type_id in
					| (select membership_type_id from membership_types where program_id = ${MagicIds.PROGRAM_TYPES.ADULT_PROGRAM_ID}))
					|""".stripMargin
		}
		val result = pb.executePreparedQueryForSelect(q)
		result.head
	}

	def getAPDiscountEligibilities(pb: PersistenceBroker, personId: Int): (Boolean, Boolean, Boolean, Boolean) = {
		val q = new PreparedQueryForSelect[(Boolean, Boolean, Boolean, Boolean)](Set(MemberUserType)) {
			override val params: List[String] = List(personId.toString, personId.toString, personId.toString, personId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Boolean, Boolean, Boolean, Boolean) =
				(rsw.getBooleanFromChar(1), rsw.getBooleanFromChar(2), rsw.getBooleanFromChar(3), rsw.getBooleanFromChar(4))

			override def getQuery: String =
				s"""
				   |select
				   |person_pkg.eligible_for_youth_online(?),
				   |person_pkg.eligible_for_senior_online(?),
				   |person_pkg.eligible_for_veteran_online(?),
				   |person_pkg.can_renew(?)
				   |from dual
				   |""".stripMargin
		}
		val result = pb.executePreparedQueryForSelect(q)
		result.head
	}

	def getDiscountActiveInstanceForDiscount(pb: PersistenceBroker, discountId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(discountId.toString)

			override def getQuery: String =
				"""
				  |select instance_id from discount_active_instances where discount_id = ?
				  |""".stripMargin
		}

		pb.executePreparedQueryForSelect(q).head
	}

	/**  @return (staggeredPaymentsAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) */
	def apSelectMemForPurchase(pb: PersistenceBroker, personId: Int, orderId: Int, memTypeId: Int, requestedDiscountInstanceId: Option[Int]): (Boolean, Boolean, Boolean, Boolean) = {
		val existingSCMs = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(personId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |select count(*) from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		val count = pb.executePreparedQueryForSelect(existingSCMs).head

		if (count == 0) {
			val insertQ = new PreparedQueryForInsert(Set(MemberUserType)) {
				override val pkName: Option[String] = Some("ITEM_ID")

				override val params: List[String] = List(
					memTypeId.toString,
					memTypeId.toString,
					personId.toString,
					orderId.toString,
					requestedDiscountInstanceId.map(_.toString).orNull
				)

				override def getQuery: String =
					"""
					  |insert into shopping_cart_memberships (
					  |      membership_type_id,
					  |      price,
					  |      person_id,
					  |      ready_to_buy,
					  |      order_id,
					  |      requested_discount_instance_id
					  |    ) values (
					  |      ?,
					  |      (select price from membership_types where membership_type_id = ?),
					  |      ?,
					  |      'N',
					  |      ?,
					  |      ?
					  |    )
					  |""".stripMargin
			}
			pb.executePreparedQueryForInsert(insertQ)
		} else {
			val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(
					memTypeId.toString,
					memTypeId.toString,
					requestedDiscountInstanceId.map(_.toString).orNull,
					personId.toString,
					orderId.toString
				)
				override def getQuery: String =
					"""
					  |update shopping_cart_memberships
					  |    set membership_type_id = ?,
					  |    price = (select price from membership_types where membership_type_id = ?),
					  |    requested_discount_instance_id = ?
					  |    where person_id = ?
					  |    and order_id = ?
					  |""".stripMargin
			}
			pb.executePreparedQueryForUpdateOrDelete(updateQ)
		}

		val paymentPlanAllowed = MembershipLogic.membershipTypeAllowsStaggeredPayments(memTypeId)

		val gpdwQuery = new PreparedQueryForSelect[(Boolean, Boolean, Boolean)](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Boolean, Boolean, Boolean) =
				(rsw.getBooleanFromChar(1), rsw.getBooleanFromChar(2), rsw.getBooleanFromChar(3))

			override def getQuery: String =
				s"""
				  |select cc_pkg.guest_privs_auto($personId, $memTypeId), cc_pkg.guest_privs_na($personId, $memTypeId), cc_pkg.damage_waiver_auto($personId, $memTypeId) from dual
				  |""".stripMargin
		}
		val (guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) = pb.executePreparedQueryForSelect(gpdwQuery).head

		(paymentPlanAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto)
	}

	def apSetGuestPrivs(pb: PersistenceBroker, personId: Int, orderId: Int, wantIt: Boolean): Unit = {
		val countQ = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(
				orderId.toString,
				personId.toString,
				orderId.toString
			)

			override def getQuery: String =
				"""
				  |select count(*) from shopping_cart_guest_privs
				  |where sc_membership_id in (select item_id from shopping_cart_memberships where order_id = ? and person_id = ?)
				  |and order_id = ?
				  |""".stripMargin
		}
		val count = pb.executePreparedQueryForSelect(countQ).head

		if (count == 0 && wantIt) {

			val scmQ = new PreparedQueryForSelect[Option[Int]](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[Int] = rsw.getOptionInt(1)

				override val params: List[String] = List(orderId.toString, personId.toString)

				override def getQuery: String =
					"""
					  |select max(item_id) from shopping_cart_memberships where order_id = ? and person_id = ?
					  |""".stripMargin
			}

			val scmId = pb.executePreparedQueryForSelect(scmQ).headOption.flatten

			lazy val realMemInsertQ = new PreparedQueryForInsert(Set(MemberUserType)) {
				override val pkName: Option[String] = Some("ITEM_ID")

				override val params: List[String] = List(personId.toString, orderId.toString)

				override def getQuery: String =
					"""
					  |insert into shopping_cart_guest_privs(
					  |      real_membership_id,
					  |      order_id,
					  |      price
					  |    ) values (
					  |      (select max(assign_id) from persons_memberships
					  |        left outer join guest_privs gp on gp.membership_id = assign_id
					  |        where gp.membership_id is null and assign_id =
					  |        (select max(assign_id) from persons_memberships where person_id = ? and void_close_id is null and membership_type_id in
					  |        (select membership_type_id from membership_types where program_id = 1)
					  |      )),
					  |      ?,
					  |      global_constant_pkg.get_value_number('GP_PRICE')
					  |    )
					  |""".stripMargin
			}

			val insertQ = scmId.fold(
				realMemInsertQ
			)(
				(scmId: Int) => new PreparedQueryForInsert(Set(MemberUserType)) {
					override val pkName: Option[String] = Some("ITEM_ID")

					override val params: List[String] = List(
						scmId.toString,
						orderId.toString
					)

					override def getQuery: String =
						"""
						  |insert into shopping_cart_guest_privs(
						  |      sc_membership_id,
						  |      order_id,
						  |      price
						  |    ) values (
						  |      ?,
						  |      ?,
						  |      global_constant_pkg.get_value_number('GP_PRICE')
						  |    )
						  |""".stripMargin
				}
			)

			pb.executePreparedQueryForInsert(insertQ)
		} else if (!wantIt) {
			val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(
					orderId.toString,
					personId.toString,
					orderId.toString
				)
				override def getQuery: String =
					"""
					  |delete from shopping_cart_guest_privs
					  |    where sc_membership_id in (select item_id from shopping_cart_memberships where order_id = ? and person_id = ?)
					  |    and order_id = ?
					  |""".stripMargin
			}
			pb.executePreparedQueryForUpdateOrDelete(deleteQ)
		}
	}

	def apSetDamageWaiver(pb: PersistenceBroker, personId: Int, orderId: Int, wantIt: Boolean): Unit = {
		val countQ = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(
				personId.toString,
				orderId.toString
			)

			override def getQuery: String =
				"""
				  |select count(*) from shopping_cart_waivers where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		val count = pb.executePreparedQueryForSelect(countQ).head

		if (count == 0 && wantIt) {
			val insertQ = new PreparedQueryForInsert(Set(MemberUserType)) {
				override val pkName: Option[String] = Some("ITEM_ID")

				override val params: List[String] = List(
					personId.toString,
					orderId.toString
				)

				override def getQuery: String =
					"""
					  |insert into shopping_cart_waivers(
					  |      person_id,
					  |      order_id,
					  |      price
					  |    ) values (
					  |      ?,
					  |      ?,
					  |      global_constant_pkg.get_value_number('DW_PRICE')
					  |    )
					  |""".stripMargin
			}

			pb.executePreparedQueryForInsert(insertQ)
		} else if (!wantIt) {
			val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(
					personId.toString,
					orderId.toString
				)
				override def getQuery: String =
					"""
					  |delete from shopping_cart_waivers
					  |    where person_id = ?
					  |    and order_id = ?
					  |""".stripMargin
			}
			pb.executePreparedQueryForUpdateOrDelete(deleteQ)
		}
	}

	def addDonationToOrder(pb: PersistenceBroker, orderId: Int, fundId: Int, amount: Double): ValidationResult = {
		if (amount <= 0) {
			ValidationResult.from("Donation amount must be >= $0.")
		} else {
			val existsQ = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(orderId.toString, fundId.toString)

				override def getQuery: String =
					"""
					  |select 1 from shopping_cart_donations where order_id = ? and fund_id = ?
					  |""".stripMargin
			}
			val exists = pb.executePreparedQueryForSelect(existsQ).nonEmpty
			if (exists) {
				val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
					override val params: List[String] = List(amount.toString, orderId.toString, fundId.toString)
					override def getQuery: String =
						"""
						  |update shopping_cart_donations
						  |set amount = ?
						  |where order_id = ? and fund_id = ?
						  |""".stripMargin
				}
				pb.executePreparedQueryForUpdateOrDelete(updateQ)
			} else {
				val insertQ = new PreparedQueryForInsert(Set(MemberUserType)) {
					override val pkName: Option[String] = Some("ITEM_ID")

					override val params: List[String] = List(
						orderId.toString,
						amount.toString,
						fundId.toString
					)

					override def getQuery: String =
						"""
						  |insert into shopping_cart_donations(
						  |      order_id,
						  |      amount,
						  |      fund_id,
						  |      initiative_id
						  |    ) values (
						  |      ?,
						  |      round(?, 2),
						  |      ?,
						  |      16
						  |    )
						  |""".stripMargin
				}
				pb.executePreparedQueryForInsert(insertQ)
			}

			ValidationOk
		}

	}

	def deleteDonationFromOrder(pb: PersistenceBroker, orderId: Int, fundId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(orderId.toString, fundId.toString)
			override def getQuery: String =
				"""
				  |delete from shopping_cart_donations
				  |where order_id = ? and fund_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(q)
	}

	def attemptAddPromoCode(pb: PersistenceBroker, orderId: Int, code: String): Unit = {
		val getDiscountQ = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(code, code)

			override def getQuery: String =
				"""
				  |select di.instance_id from discounts d, discount_active_instances dai, discount_instances di
				  |    where d.discount_id = dai.discount_id and dai.instance_id = di.instance_id
				  |	and (
				  |	   (di.is_case_sensitive = 'Y' and di.universal_code = ?)
				  |	 or
				  |	   (nvl(di.is_case_sensitive,'N') <> 'Y' and lower(di.universal_code) = lower(?))
				  |	)
				  |""".stripMargin
		}

		val instances = pb.executePreparedQueryForSelect(getDiscountQ)
		if (instances.nonEmpty) {
			val instanceId = instances.head
			val existsQ = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(orderId.toString, instanceId.toString)

				override def getQuery: String =
					"""
					  |select 1 from orders_promos where order_id = ? and instance_id = ?
					  |""".stripMargin
			}
			if (pb.executePreparedQueryForSelect(existsQ).isEmpty) {
				val insertQ = new PreparedQueryForInsert(Set(MemberUserType)) {
					override val pkName: Option[String] =Some("ASSIGN_ID")

					override val params: List[String] = List(orderId.toString, instanceId.toString)

					override def getQuery: String =
						"""
						  |insert into orders_promos(
						  |	    order_id,
						  |		instance_id
						  |	  ) values (
						  |	    ?,
						  |		?
						  |	  )
						  |""".stripMargin
				}
				pb.executePreparedQueryForInsert(insertQ)
			}
		}
	}

	def addGiftCertificateToOrder(pb: PersistenceBroker, gcNumber: Int, gcCode: String, orderId: Int, now: LocalDate): ValidationResult = {
		val validQ = new PreparedQueryForSelect[(Int, LocalDate)](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, LocalDate) =
				(rsw.getInt(1), rsw.getLocalDate(2))

			override val params: List[String] = List(gcNumber.toString, gcCode, orderId.toString)

			override def getQuery: String =
				"""
				  |  select gc.cert_id, trunc(gcp.expiration_date)
				  |  from gift_certificates gc, gift_cert_current_states gccs, gift_cert_purchases gcp
				  |  where gc.cert_id = gccs.cert_id and gc.cert_id = gcp.cert_id
				  |  and gc.cert_number = ?
				  |  and upper(gc.redemption_code) = upper(?)
				  |  and (
				  |    gccs.membership_type_id is not null or nvl(gccs.value,0) > 0
				  |  )
				  |
				  |  minus
				  |
				  |  select sc.cert_id, trunc(gcp.expiration_date)
				  |  from shopping_cart_appl_gc sc, gift_cert_purchases gcp
				  |  where sc.cert_id = gcp.cert_id
				  |  and sc.order_id = ?
				  |""".stripMargin
		}
		val certs = pb.executePreparedQueryForSelect(validQ)
		if (certs.nonEmpty) {
			val (certId, certExp) = certs.head
			if (now.isAfter(certExp)) {
				ValidationResult.from("That gift certificate has expired.")
			} else {
				val proc = new PreparedProcedureCall[Unit](Set(MemberUserType)) {

					override def setInParametersInt: Map[String, Int] = Map(
						"P_CERT_ID" -> certId,
						"p_order_id" -> orderId
					)

					override def getQuery: String = "cc_pkg.apply_gc(?, ?)"

					override def registerOutParameters: Map[String, Int] = Map.empty

					override def getOutResults(cs: CallableStatement): Unit = Unit
				}
				pb.executeProcedure(proc)
				ValidationOk
			}
		} else {
			ValidationResult.from("Gift certificate could not be redeemed; please try again.")
		}
	}

	def unapplyGCFromOrder(pb: PersistenceBroker, orderId: Int, certId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override val params: List[String] = List(certId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_appl_gc where cert_id = ? and order_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(q)
	}

	def apCanClaim(pb: PersistenceBroker, email: String): Either[String, Int] = {
		val proc = new PreparedProcedureCall[Either[String, Int]](Set(PublicUserType, BouncerUserType)) {
			//					procedure ap_claim_by_email_proc(
			//							i_email in varchar2,
			//							o_person_id out number,
			//							o_error out varchar2
			//					) as

			override def setInParametersVarchar: Map[String, String] = Map(
				"i_email" -> email,
			)

			override def getQuery: String = "person_pkg.ap_claim_by_email_proc(?, ?, ?)"

			override def registerOutParameters: Map[String, Int] = Map(
				"o_person_id" -> java.sql.Types.INTEGER,
				"o_error" -> java.sql.Types.VARCHAR
			)

			override def getOutResults(cs: CallableStatement): Either[String, Int] = {
				val error = cs.getString("o_error")
				if (error != null) {
					Left(error)
				} else {
					Right(cs.getInt("o_person_id"))
				}
			}
		}
		pb.executeProcedure(proc)
	}

	def apDoClaim(pb: PersistenceBroker, personId: Int): Unit = {
		val proc = new PreparedProcedureCall[Unit](Set(BouncerUserType)) {
//			procedure ap_verify_access(
//					p_person_id in number,
//					p_app_id in number default 610
//			)

			override def setInParametersInt: Map[String, Int] = Map(
				"p_person_id" -> personId,
				"p_app_id" -> -1
			)

			override def getQuery: String = "email_pkg.ap_verify_access(?, ?)"

			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = Unit
		}
		pb.executeProcedure(proc)
	}

	def validateClaimAcctHash(pb: PersistenceBroker, email: String, personId: Int, hash: String): ValidationResult = {
		val q = new PreparedQueryForSelect[Int](Set(BouncerUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(
				personId.toString,
				email,
				personId.toString,
				hash
			)

			override def getQuery: String =
				"""
				  |select 1 from persons where person_id = ? and lower(email) = lower(?) and pw_hash is null and public_auth.hash('AP_EMAIL',?) = ?
				  |""".stripMargin
		}
		val exists = pb.executePreparedQueryForSelect(q).nonEmpty
		if (exists) ValidationOk
		else ValidationResult.from("An internal error occurred; if this message persists please contact CBI at 617-523-1038")
	}

	def getAllMembershipPrices(pb: PersistenceBroker): List[(Int, Double)] = {
		val q = new PreparedQueryForSelect[(Int, Double)](Set(PublicUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, Double) =
				(rsw.getInt(1), rsw.getDouble(2))

			override def getQuery: String =
				"""
				  |select
				  |membership_type_id,
				  |price
				  |from membership_types where active = 'Y'
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	def getDwGpPrices(pb: PersistenceBroker): (Double, Double) = {
		val q = new PreparedQueryForSelect[(Double, Double)](Set(PublicUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Double, Double) =
				(rsw.getDouble(1), rsw.getDouble(2))

			override def getQuery: String =
				"""
				  |select
				  |global_constant_pkg.get_value_number('DW_PRICE'),
				  |global_constant_pkg.get_value_number('GP_PRICE')
				  |from dual
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).head
	}

	def getAllDiscounts(pb: PersistenceBroker): List[(Int, Int, Double)] = {
		val q = new PreparedQueryForSelect[(Int, Int, Double)](Set(PublicUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, Int, Double) =
				(rsw.getInt(1), rsw.getInt(2), rsw.getDouble(3))

			override def getQuery: String =
				"""
				  |select
				  |md.type_id,
				  |dai.discount_id,
				  |md.discount_amt
				  |from discount_active_instances dai, memberships_discounts md
				  |where dai.instance_id = md.instance_id
				  |and md.discount_amt is not null
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	def getApClassTypeAvailabilities(pb: PersistenceBroker, personId: Int): List[ApClassAvailability] = {
		val q = new PreparedQueryForSelect[ApClassAvailability](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ApClassAvailability = ApClassAvailability(
				typeName=rsw.getString(2),
				typeId=rsw.getInt(1),
				availabilityFlag=rsw.getInt(4),
				displayOrder = rsw.getDouble(3),
				noSignup = rsw.getOptionBooleanFromChar(5).getOrElse(false),
				seeTypeError = rsw.getOptionString(6),
				description = rsw.getString(7)
			)

			override val params: List[String] = List(personId.toString, personId.toString)

			override def getQuery: String =
				"""
				  |select
				  |type_id,
				  |type_name,
				  |display_order,
				  |ap_class_pkg.get_type_visibility(?, type_id),
				  |t.no_signup,
				  |ap_class_pkg.see_type(?, t.type_id),
				  |t.desc_long
				  |from ap_class_types t
				  |order by t.display_order
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}
	case class ApClassAvailability (
		typeId: Int,
		typeName: String,
		availabilityFlag: Int,
		displayOrder: Double,
		noSignup: Boolean,
		seeTypeError: Option[String],
		description: String
	)

	object ApClassAvailability {
		implicit val format = Json.format[ApClassAvailability]

		def apply(v: JsValue): ApClassAvailability = v.as[ApClassAvailability]
	}

	def getApClassesForCalendar(pb: PersistenceBroker, personId: Int): List[ApClassInstanceForCalendar] = {
		val instancesQ: PreparedQueryForSelect[ApClassInstanceForCalendar] = new PreparedQueryForSelect[ApClassInstanceForCalendar](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ApClassInstanceForCalendar = ApClassInstanceForCalendar(
				instanceId = rsw.getInt(2),
				typeName = rsw.getString(1),
				sessions = List.empty,
				signupType = rsw.getOptionString(3),
				waitlistResult = rsw.getOptionString(4),
				typeId = rsw.getInt(5),
				seeInstanceError = rsw.getOptionString(6),
				spotsLeft = rsw.getInt(7),
				price = rsw.getOptionDouble(8).getOrElse(0),
			)

			override val params: List[String] = List(personId.toString, personId.toString)

			override def getQuery: String =
				"""
				  |select
				  |t.type_name,
				  |i.instance_id,
				  |si.signup_type,
				  |wlr.wl_result,
				  |t.type_id,
				  |ap_class_pkg.see_instance(?, i.instance_id),
				  |ap_class_pkg.spots_left(i.instance_id),
				  |i.price
				  |from ap_class_types t, ap_class_formats f, ap_class_bookends bk, ap_class_sessions fs, ap_class_instances i
				  |left outer join ap_class_signups si
				  |on si.instance_id = i.instance_id and si.person_id = ?
				  |and (si.signup_type is null or si.signup_type not in ('U','A'))
				  |left outer join ap_class_wl_results wlr
				  |on si.signup_id = wlr.signup_id
				  |where i.format_id = f.format_id and f.type_id = t.type_id
				  |and i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				  |and fs.session_datetime > (add_months(util_pkg.get_sysdate, -18))
				  |and fs.session_datetime < (add_months(util_pkg.get_sysdate, 6))
				  |and i.cancelled_datetime is null
				  |and nvl(i.hide_online,'N') <> 'Y'
				  |order by fs.session_datetime
				  |
				  |""".stripMargin
		}

		val instances = pb.executePreparedQueryForSelect(instancesQ, 2000)
		val instanceIDs = instances.map(_.instanceId)
		val sessionsQ = new PreparedQueryForSelect[ApClassSessionForCalendar](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ApClassSessionForCalendar = ApClassSessionForCalendar(
				sessionId = rsw.getInt(1),
				instanceId = rsw.getInt(2),
				sessionDatetime = rsw.getLocalDateTime(3),
				sessionLength = rsw.getDouble(4)

			)

			override def getQuery: String =
				s"""
				  |select
				  |s.session_id,
				  |s.instance_id,
				  |s.session_Datetime,
				  |s.session_length
				  |from ap_class_sessions s, ap_class_instances i
				  |where s.instance_id = i.instance_id
				  |and s.session_datetime > (add_months(util_pkg.get_sysdate, -18))
				  |and s.session_datetime < (add_months(util_pkg.get_sysdate, 6))
				  |and i.cancelled_datetime is null
				  |and nvl(i.hide_online,'N') <> 'Y'
				  |order by s.session_datetime
				  |""".stripMargin
		}
		val sessions = pb.executePreparedQueryForSelect(sessionsQ, 2000)
		instances.map(i => {
			val sessionsForInstance = sessions.filter(_.instanceId == i.instanceId)
			i.copy(sessions = sessionsForInstance)
		}).filter(i => i.sessions.nonEmpty)
	}

	case class ApClassSessionForCalendar(
		sessionId: Int,
		instanceId: Int,
		sessionDatetime: LocalDateTime,
		sessionLength: Double
	)

	object ApClassSessionForCalendar {
		implicit val format = Json.format[ApClassSessionForCalendar]

		def apply(v: JsValue): ApClassSessionForCalendar = v.as[ApClassSessionForCalendar]
	}

	case class ApClassInstanceForCalendar (
		instanceId: Int,
		typeName: String,
		typeId: Int,
		sessions: List[ApClassSessionForCalendar],
		signupType: Option[String],
		waitlistResult: Option[String],
		seeInstanceError: Option[String],
		spotsLeft: Int,
		price: Double
	)

	object ApClassInstanceForCalendar{
		implicit val format = Json.format[ApClassInstanceForCalendar]

		def apply(v: JsValue): ApClassInstanceForCalendar = v.as[ApClassInstanceForCalendar]
	}

	def apClassSignup(pb: PersistenceBroker, personId: Int, instanceId: Int, isWaitlist: Boolean): Option[String] = {
		val ppc = new PreparedProcedureCall[String](Set(MemberUserType)) {
//			procedure do_signup(
//					i_person_id in number,
//					i_instance_id in number,
//					i_signup_type in char,
//					i_do_override in char default 'N',
//					i_order_id in number,
//					i_payment_medium in varchar2,
//					o_signup_type out char,
//					o_error_msg out clob
//			)
			override def registerOutParameters: Map[String, Int] = Map(
				"o_signup_type" -> java.sql.Types.CHAR,
				"o_error_msg" -> java.sql.Types.CLOB
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_instance_id" -> instanceId,
				"i_order_id" -> getOrderId(pb, personId),
			)


			override def setInParametersVarchar: Map[String, String] = Map(
				"i_signup_type" -> { if (isWaitlist) "W" else "E" },
				"i_do_override" -> "N",
				"i_payment_medium" -> null,
			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error_msg")

			override def getQuery: String = "ap_class_pkg.do_signup(?, ?, ?, ?, ?, ?, ?, ?)"
		}

		pb.executeProcedure(ppc) match {
			case null => None
			case s: String => Some(s)
		}
	}

	def apClassUnenroll(pb: PersistenceBroker, personId: Int, instanceId: Int): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(MemberUserType)) {
//			procedure cancel_signup(
//					p_person_id in number,
//					p_instance_id in number,
//					p_cancel_type in char,
//					p_is_online in char
//			)
			override def setInParametersInt: Map[String, Int] = Map(
				"p_person_id" -> personId,
				"p_instance_id" -> instanceId,
			)


			override def setInParametersVarchar: Map[String, String] = Map(
				"p_cancel_type" -> "U",
				"p_is_online" -> "Y",
			)

			override def getQuery: String = "ap_class_pkg.cancel_signup(?, ?, ?, ?)"

			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = Unit
		}

		pb.executeProcedure(ppc)
	}

	def getStripeCustomerId(pb: PersistenceBroker, personId: Int): Option[String] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override val params: List[String] = List(personId.toString)
			override def getQuery: String =
				"""
				  |select stripe_customer_id from persons where person_id = ?
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q).head
	}

	def getAllMembershipTypesWithPrices(pb: PersistenceBroker): List[(Int, Option[Currency])]  = {
		val q = new PreparedQueryForSelect[(Int, Option[Currency])](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, Option[Currency]) =
				(rsw.getInt(1), rsw.getOptionDouble(2).map(Currency.dollars))

			override def getQuery: String =
				"""
				  |select membership_type_id, price from membership_types
				  |""".stripMargin
		}
		pb.executePreparedQueryForSelect(q)
	}

	def canRenewOnEachDate(pb: PersistenceBroker, personId: Int, dates: List[LocalDate]): List[(LocalDate, Boolean)] = {
		type CanRenewOnDate = (LocalDate, Boolean)
		val q = new PreparedQueryForSelect[CanRenewOnDate](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (LocalDate, Boolean) =
				(rsw.getLocalDate(1), rsw.getBooleanFromChar(2))

			override def getQuery: String = dates.map(d => {
				s"select ${GetSQLLiteralPrepared(d)}, person_pkg.can_renew($personId, ${GetSQLLiteralPrepared(d)}) from dual"
			}).mkString(" UNION ALL ")
		}
		pb.executePreparedQueryForSelect(q)
	}

	def getPaymentPlansForMembershipInCart(pb: PersistenceBroker, personId: Int, orderId: Int, now: LocalDate): List[List[(LocalDate, Currency)]] = {
		/** typeId, price, discountId, discountAmt */
		type SCM = (Int, Double, Option[Int], Option[Double])

		val scm = {
			val q = new PreparedQueryForSelect[SCM](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): SCM =
					(rsw.getInt(1), rsw.getDouble(2), rsw.getOptionInt(3), rsw.getOptionDouble(4))

				override def getQuery: String =
					s"""
					  |select membership_type_id, price, di.discount_id, discount_amt
					  |from shopping_cart_memberships scm left outer join discount_instances di
					  |on scm.discount_instance_id = di.instance_id
					  |where order_id = $orderId and person_id = $personId
					  |""".stripMargin
			}
			val scms = pb.executePreparedQueryForSelect(q)
			if (scms.length != 1) None
			else Some(scms.head)
		}

		scm match {
			case None => List.empty
			case Some((typeId, price, discountIdOption, discountAmtOption)) => {
				val priceAsCurrency = Currency.dollars(price)

				// return a function mapping end date to price
				// for renewals, if the end date is past their "can renew" date,
				// then the price is the full price ie the current price plus the renewal discoutn amt added back on
				// Otherwise its just the price that SCM says it is
				val endDateToPrice = (endDates: List[LocalDate]) => {
					val samePrice: LocalDate => Currency = _ => priceAsCurrency

					discountIdOption match {
						case Some(MagicIds.DISCOUNTS.RENEWAL_DISCOUNT_ID) => {

							// map of (proposed payment plan end date) => (can the user still renew on that date)
							val canRenewOnEachDate = PortalLogic.canRenewOnEachDate(pb, personId, endDates).toMap

							// return a fn that takes a date and checks it against the map above.
							// If they cant renew, put the discount amt back on the price
							(ld: LocalDate) => {
								if (canRenewOnEachDate(ld)) {
									priceAsCurrency - Currency.dollars(discountAmtOption.getOrElse(0d))
								} else {
									priceAsCurrency
								}
							}
						}
						case _ => samePrice
					}
				}
				MembershipLogic.calculateAllPaymentSchedules(now, endDateToPrice)
			}
		}
	}
}


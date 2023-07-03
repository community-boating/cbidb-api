package org.sailcbi.APIServer.IO.Portal

import com.coleji.neptune.API.{ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{PermissionsAuthority, RequestCache}
import com.coleji.neptune.IO.PreparedQueries._
import com.coleji.neptune.Storable.{GetSQLLiteral, GetSQLLiteralPrepared, ResultSetWrapper}
import com.coleji.neptune.Util._
import com.coleji.neptune.{API, Storable}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Member.ApClassInstancesInstructorInfo.MemberApClassInstancesInstructorInfoGetResponseSuccessDto
import org.sailcbi.APIServer.BarcodeFactory
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{PaymentIntent, PaymentMethod}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.Entities.dto.PersonNotification
import org.sailcbi.APIServer.Entities.dto.PersonNotification.{PersonAllNotificationsDto, PersonNotificationDbRecord}
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.GetCurrentOnlineClose
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Logic.MembershipLogic
import org.sailcbi.APIServer.UserTypes._
import play.api.libs.json.{JsValue, Json}

import java.awt.image.BufferedImage
import java.sql.CallableStatement
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PortalLogic {
	def persistStandalonePurchaser(rc: RequestCache, cookieValue: String, maybePersonId: Option[Int], nameFirst: Option[String], nameLast: Option[String], email: Option[String]): Int = {
		maybePersonId match {
			case Some(personId) => {
				val pq = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
					override val params: List[String] = List(
						nameFirst.orNull,
						nameLast.orNull,
						email.orNull
					)

					override def getQuery: String =
						s"""
						  |update persons set
						  |name_first = ?,
						  |name_last = ?,
						  |email = ?
						  |where person_id = $personId
						  |
						  |""".stripMargin
				}
				rc.executePreparedQueryForUpdateOrDelete(pq)
				personId
			}
			case None => {
				val pq = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
					override val params: List[String] = List(
						cookieValue,
						nameFirst.orNull,
						nameLast.orNull,
						email.orNull
					)
					override val pkName: Option[String] = Some("PERSON_ID")

					override def getQuery: String =
						s"""
						   |insert into persons (temp, protoperson_cookie, proto_state, name_first, name_last, email)
						   |values ('P', ?, 'I', ?, ?, ?)
						   |""".stripMargin
				}
				val personId = rc.executePreparedQueryForInsert(pq).get.toInt
				personId
			}
		}

	}
	def persistProtoParent(rc: RequestCache, cookieValue: String): Int = {
		val pq = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
			override val params: List[String] = List(cookieValue)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				s"""
				  |insert into persons (temp, protoperson_cookie, proto_state, person_type) values ('P', ?, 'I', ${MagicIds.PERSON_TYPE.JP_PARENT})
				  |""".stripMargin
		}
		val parentId = rc.executePreparedQueryForInsert(pq).get.toInt
		getOrderIdJP(rc, parentId)
		parentId
	}
	def persistProtoJunior(rc: RequestCache, parentPersonId: Int, firstName: String, hasClassReservations: Boolean): (Int, () => Unit) = {
		val createJunior = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
			override val params: List[String] = List(firstName)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				"""
				  |insert into persons (temp, name_first, proto_state) values ('P', ?, 'I')
				  |""".stripMargin
		}
		val juniorPersonId = rc.executePreparedQueryForInsert(createJunior).get.toInt

		val createAcctLink = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
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
		rc.executePreparedQueryForInsert(createAcctLink)

		val orderId = getOrderIdJP(rc, parentPersonId)

		val createSCM = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
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

		rc.executePreparedQueryForInsert(createSCM)

		val rollback = () => {
			val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List(juniorPersonId.toString, orderId.toString)

				override def getQuery: String =
					"""
					  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
					  |""".stripMargin
			}
			val deletePersonRelationship = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from person_relationships where b = ?
					  |""".stripMargin
			}
			val deletePerson = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from persons where person_id = ?
					  |""".stripMargin
			}
			rc.executePreparedQueryForUpdateOrDelete(deleteSCM)
			rc.executePreparedQueryForUpdateOrDelete(deletePersonRelationship)
			val deletedCt = rc.executePreparedQueryForUpdateOrDelete(deletePerson)
		}
		(juniorPersonId, rollback)
	}

	def deleteProtoJunior(rc: RequestCache, parentPersonId: Int, firstName: String): Either[String, String] = {
		val matchingIdsQuery = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
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

		val ids = rc.executePreparedQueryForSelect(matchingIdsQuery)
		if (ids.nonEmpty) {
			val deleteSignups = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from jp_class_signups where person_id in (${ids.mkString(", ")})
					  |""".stripMargin
			}
			val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from shopping_cart_memberships where person_id in (${ids.mkString(", ")})
					  |""".stripMargin
			}
			val deletePersonRelationship = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from person_relationships where b in (${ids.mkString(", ")})
					  |""".stripMargin
			}
			val deletePerson = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List()

				override def getQuery: String =
					s"""
					  |delete from persons where person_id in (${ids.mkString(", ")})
					  |""".stripMargin
			}

			rc.executePreparedQueryForUpdateOrDelete(deleteSignups)
			rc.executePreparedQueryForUpdateOrDelete(deleteSCM)
			rc.executePreparedQueryForUpdateOrDelete(deletePersonRelationship)
			rc.executePreparedQueryForUpdateOrDelete(deletePerson)
			Right(ids.mkString(", "))
		} else {
			Left("Unable to locate distinct junior to delete")
		}
	}

	def getMinSignupTimeForParent(rc: RequestCache, parentPersonId: Int): Option[LocalDateTime] = {
		val q = new PreparedQueryForSelect[Option[LocalDateTime]](Set(ProtoPersonRequestCache)) {
			override val params: List[String] = List(parentPersonId.toString)

			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Option[LocalDateTime] = rs.getOptionLocalDateTime(1)

			override def getQuery: String =
				"""
				  |select min(signup_datetime) from jp_class_signups si, person_relationships rl
				  |where si.person_id = rl.b and rl.a = ? and si.signup_type = 'P'
				  |""".stripMargin
		}
		val list = rc.executePreparedQueryForSelect(q)
		println(list)
		list.headOption.flatten
	}

	def spotsLeft(rc: RequestCache, instanceId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.spots_left(?) from dual
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).headOption.getOrElse(0)
	}

	def actualSpotsLeft(rc: RequestCache, instanceId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.actual_spots_left(?) from dual
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).headOption.getOrElse(0)
	}

	def hasSpotsLeft(rc: RequestCache, instanceId: Int, messageOverride: Option[String]): ValidationResult = {
		if (spotsLeft(rc, instanceId) > 0) ValidationOk
		else ValidationResult.from(messageOverride.getOrElse("This class is full."))
	}

	def hasActualSpotsLeft(rc: RequestCache, instanceId: Int, messageOverride: Option[String]): ValidationResult = {
		if (actualSpotsLeft(rc, instanceId) > 0) ValidationOk
		else ValidationResult.from(messageOverride.getOrElse("This class is full."))
	}

	def alreadyStarted(rc: RequestCache, instanceId: Int): Either[String, Unit] = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select bk.instance_id from jp_class_bookends bk, jp_class_sessions fs
				  |where bk.first_session = fs.session_id and bk.instance_id = ?
				  |and fs.session_datetime > util_pkg.get_sysdate
				  |""".stripMargin
		}
		if (rc.executePreparedQueryForSelect(q).nonEmpty) Right() else Left("That class has already started.")
	}

	def alreadyStartedAsValidationResult(rc: RequestCache, instanceId: Int): ValidationResult = {
		alreadyStarted(rc, instanceId) match {
			case Left(s) => ValidationResult.from(s)
			case Right(_) => ValidationOk
		}
	}

	def seeTypeFromInstanceId(rc: RequestCache, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_type(?, i.type_id) from jp_class_instances i where i.instance_id = ?
				  |""".stripMargin
		}
		val queryResult = rc.executePreparedQueryForSelect(q).headOption
		val ret = queryResult.getOrElse('N') == "Y"
		println("seeTypeFromInstanceId: queryResult: " + queryResult + "; returning " + ret)
		ret
	}

	def seeTypeFromInstanceIdAsValidationResult(rc: RequestCache, juniorId: Int, instanceId: Int): ValidationResult = {
		if (seeTypeFromInstanceId(rc, juniorId, instanceId)) ValidationOk
		else ValidationResult.from("You are not eligible for this class type.")
	}

	def seeInstance(rc: RequestCache, juniorId: Int, instanceId: Int): Boolean = {
		val q = new PreparedQueryForSelect[String](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.see_instance(?, ?) from dual
				  |""".stripMargin
		}
		val queryResult = rc.executePreparedQueryForSelect(q).headOption
		val ret = queryResult.getOrElse('N') == "Y"
		println("seeInstance: queryResult: " + queryResult + "; returning " + ret)
		ret
	}

	def seeInstanceAsValidationResult(rc: RequestCache, juniorId: Int, instanceId: Int): ValidationResult = {
		if (seeInstance(rc, juniorId, instanceId)) ValidationOk
		else ValidationResult.from("You are not eligible for this class.")
	}

	def allowEnroll(rc: RequestCache, juniorId: Int, instanceId: Int): Option[String] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override def getQuery: String =
				"""
				  |select jp_class_pkg.allow_enroll(?, ?) from dual
				  |""".stripMargin
		}
		val ret = rc.executePreparedQueryForSelect(q).headOption.flatten
		println("allow enroll result: " + ret)
		ret
	}

	def allowEnrollAsValidationResult(rc: RequestCache, juniorId: Int, instanceId: Int): ValidationResult = {
		allowEnroll(rc, juniorId, instanceId) match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	// either an error message or a rollback function
	def attemptSingleClassSignupReservation(
		rc: RequestCache,
		juniorPersonId: Int,
		instanceId: Int,
		signupDatetime: Option[LocalDateTime],
		orderId: Int
	)(implicit pa: PermissionsAuthority): Either[String, () => Unit] = {
		lazy val hasSpots = {
			val spots = PortalLogic.spotsLeft(rc, instanceId)
			if (spots > 0) Right(spots)
			else Left("The class has no open seats at this time. In order to proceed with purchasing a membership you must register for an open class that fits with your schedule.")
		}

		lazy val alreadyStarted = PortalLogic.alreadyStarted(rc, instanceId)

		lazy val seeType = if(PortalLogic.seeTypeFromInstanceId(rc, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class type.")
		lazy val seeInstance = if(PortalLogic.seeInstance(rc, juniorPersonId, instanceId)) Right() else Left("You are not eligible for this class.")
		lazy val allowEnroll = PortalLogic.allowEnroll(rc, juniorPersonId, instanceId) match {
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
			val signupId = actuallyEnroll(rc, instanceId, juniorPersonId, signupDatetime, doEnroll = true, fullEnroll = false, Some(orderId))
			println("inserted, got back signup id " + signupId)
			// return a rollback fn
			() => {
				println("Rolling back " + signupId.get)
				val rollbackQ = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
					override val params: List[String] = List(signupId.get)

					override def getQuery: String =
						"""
						  |delete from jp_class_signups where signup_id = ?
						  |""".stripMargin
				}
				rc.executePreparedQueryForUpdateOrDelete(rollbackQ)
			}
		})
	}

	def actuallyEnroll(
		rc: RequestCache,
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

		val proc = new PreparedProcedureCall[Int](Set(ProtoPersonRequestCache, MemberRequestCache)) {
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
				"p_send_email" -> "Y",
				"p_enroll_type" -> enrollType,
				"p_do_fallback_wl" -> "N",
				"p_signup_datetime" -> signupDatetime.map(_.format(DateUtil.DATE_TIME_FORMATTER)).orNull
			)

			override def getOutResults(cs: CallableStatement): Int = cs.getInt("o_signup_id")
			override def getQuery: String = "jp_class_pkg.signup_core(?, ?, ?, ?, ?, ?, ?, ?, ?)"
		}

		Some(rc.executeProcedure(proc).toString)
	}

	// Return Some(error message) on fail, None on success
	def attemptSignupReservation(
		rc: RequestCache,
		juniorPersonId: Int,
		beginnerInstanceId: Option[Int],
		intermediate1InstanceId: Option[Int],
		intermediate2InstanceId: Option[Int],
		signupDatetime: Option[LocalDateTime],
		orderId: Int
	): Option[String] = {
		// Make each of these initializables; they will only be initialized if the elements before them in the for comp were successes
		// If it's not initialized then we know it was never run, and therefore we don't need to run its rollback
		val beginnerResult = new DefinedInitializableNullary(() => beginnerInstanceId match {
			case None => Right(() => {})
			case Some(id) => PortalLogic.attemptSingleClassSignupReservation(rc, juniorPersonId, id, signupDatetime, orderId)
		})

		val intermediate1Result = new DefinedInitializableNullary(() => intermediate1InstanceId match {
			case None => Right(() => {})
			case Some(id) => PortalLogic.attemptSingleClassSignupReservation(rc, juniorPersonId, id, signupDatetime, orderId)
		})

		val intermediate2Result = new DefinedInitializableNullary(() => intermediate2InstanceId match {
			case None => Right(() => {})
			case Some(id) => PortalLogic.attemptSingleClassSignupReservation(rc, juniorPersonId, id, signupDatetime, orderId)
		})

		val totalResult = for {
			_ <- beginnerResult.get()
			_ <- intermediate1Result.get()
			x <- intermediate2Result.get()
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
			println("was a fail")
			intermediate1Result.forEach({
				case Right(rollback) => {
					println("should be calling the rollback...")
					rollback()
				}
				case _ =>
			})
			Some(totalResult.swap.getOrElse(""))
		} else None
	}

	def pruneOldReservations(rc: RequestCache): Int = {
		val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override val params: List[String] = List.empty

			override def getQuery: String =
				s"""
				  |delete from jp_class_signups
				  |where signup_type = 'P'
				  |and signup_datetime + (jp_class_pkg.get_minutes_to_reserve_class / (24 * 60)) < util_pkg.get_sysdate
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	def getOrderIdAP(rc: RequestCache, personId: Int): Int = getOrderId(rc, personId, MagicIds.ORDER_NUMBER_APP_ALIAS.AP)

	def getOrderIdJP(rc: RequestCache, personId: Int): Int = getOrderId(rc, personId, MagicIds.ORDER_NUMBER_APP_ALIAS.JP)

	@deprecated
	def getOrderId(rc: RequestCache, personId: Int): Int = getOrderId(rc, personId, MagicIds.ORDER_NUMBER_APP_ALIAS.SHARED)

	def getOrderId(rc: RequestCache, personId: Int, appAlias: String): Int = {
		val pc = new PreparedProcedureCall[Int](Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
//			procedure get_or_create_order_id(
//					i_person_id in number,
//					i_app_alias in varchar2,
//					o_order_id out number
//			) ;
			override def registerOutParameters: Map[String, Int] = Map(
				"o_order_id" -> java.sql.Types.INTEGER
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId
			)


			override def setInParametersVarchar: Map[String, String] = Map(
				"i_app_alias" -> appAlias
			)
			override def setInParametersDouble: Map[String, Double] = Map.empty
			override def getOutResults(cs: CallableStatement): Int = cs.getInt("o_order_id")
			override def getQuery: String = "cc_pkg.get_or_create_order_id(?, ?, ?)"
		}
		val ret = rc.executeProcedure(pc)
		if (ret == 0) throw new Exception("No order ID created")
		else ret
	}

	def getCardData(rc: RequestCache, orderId: Int): Option[StripeTokenSavedShape] = {
		val cardDataQ = new PreparedQueryForSelect[StripeTokenSavedShape](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override val params: List[String] = List(orderId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): StripeTokenSavedShape = new StripeTokenSavedShape(
				token = rsw.getString(2),
				orderId = rsw.getInt(1),
				last4 = rsw.getString(3),
				expMonth = rsw.getInt(4),
				expYear = rsw.getInt(5),
				zip = rsw.getOptionString(6)
			)

			override def getQuery: String =
				"""
				  |select ORDER_ID, TOKEN, CARD_LAST_DIGITS, CARD_EXP_MONTH, CARD_EXP_YEAR, CARD_ZIP
				  |from active_stripe_tokens where order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(cardDataQ).headOption
	}

	def getOrderTotalDollars(rc: RequestCache, orderId: Int): Double = {

		val orderTotalQ = new PreparedQueryForSelect[Double](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override val params: List[String] = List(orderId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Double = rsw.getOptionDouble(1).getOrElse(0)

			override def getQuery: String = "select cc_pkg.calculate_total(?) from dual"
		}
		rc.executePreparedQueryForSelect(orderTotalQ).head
	}

	def getOrderTotalCents(rc: RequestCache, orderId: Int): Int =
		Currency.toCents(getOrderTotalDollars(rc, orderId))

	def waitListExists(rc: RequestCache, instanceId: Int): ValidationResult = {
		val wlExists = rc.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberRequestCache)) {
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

	def allowWaitList(rc: RequestCache, personId: Int, instanceId: Int): ValidationResult = {
		val error = rc.executePreparedQueryForSelect(new PreparedQueryForSelect[Option[String]](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Option[String] = rs.getString(1) match {
				case "" | null => None
				case s => Some(s)
			}

			override def getQuery: String =
				s"""
				   |select jp_class_pkg.allow_wl(?, ?) from dual
				   |""".stripMargin

			override val params: List[String] = List(personId.toString, instanceId.toString)
		}).head

		error match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	def getTypeIdForInstanceId(rc: RequestCache, instanceId: Int): Int = {
		val typeIdQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String = "select type_id from jp_class_instances where instance_id = ?"

			override val params: List[String] = List(instanceId.toString)
		}

		rc.executePreparedQueryForSelect(typeIdQ).head
	}

	def getConflictingSignups(rc: RequestCache, personId: Int, instanceId: Int, enrollment: Boolean): List[Int] = {
		val typeId = getTypeIdForInstanceId(rc, instanceId)

		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(typeId.toString, instanceId.toString, personId.toString)

			override def getQuery: String =
				s"""
				  |select s.instance_id from jp_class_signups s, jp_class_instances i, jp_class_bookends bk, jp_class_sessions fs
				  |    where s.instance_id = i.instance_id and i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				  |    and fs.session_datetime > util_pkg.get_sysdate
				  |    and i.type_id = ?
				  |    and i.instance_id <> ?
				  |    and s.person_id = ?
				  |    and signup_type = '${if (enrollment) "E" else "W"}'
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q)
	}

	def deleteConflictingSignups(rc: RequestCache, juniorId: Int, instanceId: Int, instanceIdToKeep: Int, enrollment: Boolean): Unit = {
		val instanceIdsToDelete = getConflictingSignups(rc, juniorId, instanceId, enrollment).filter(_ != instanceIdToKeep)

		val signupsQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				   |select signup_id from jp_class_signups
				   |where person_id = ${juniorId}
				   |and instance_id in (${instanceIdsToDelete.mkString(", ")})
				   |
				   |""".stripMargin

		}

		val signupIds = rc.executePreparedQueryForSelect(signupsQ)

		val wlQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				   |delete from jp_class_wl_results
				   |where signup_id in (${signupIds.mkString(", ")})
				   |
				   |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(wlQ)

		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				  |delete from jp_class_signups
				  |where signup_id in (${signupIds.mkString(", ")})
				  |
				  |""".stripMargin
		}

		val deletedCt = rc.executePreparedQueryForUpdateOrDelete(q)
		println("deleted " + deletedCt)
	}

	def canWaitListJoin(rc: RequestCache, juniorId: Int, instanceId: Int): Boolean = {
		rc.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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

	def attemptDeleteSignup(rc: RequestCache, juniorId: Int, instanceId: Int): ValidationResult = {
		val proc = new PreparedProcedureCall[Option[String]](Set(MemberRequestCache)) {
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
		rc.executeProcedure(proc) match {
			case None => ValidationOk
			case Some(s) => ValidationResult.from(s)
		}
	}

	case class SignupForReport(instanceId: Int, typeId: Int, className: String, week: String, dateString: String, timeString: String)
	object SignupForReport {

		implicit val format = Json.format[SignupForReport]

		def apply(v: JsValue): SignupForReport = v.as[SignupForReport]
	}

	def getSignupsForReport(rc: RequestCache, juniorId: Int): List[SignupForReport] = {
		val q = new PreparedQueryForSelect[SignupForReport](Set(MemberRequestCache)) {
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
				  |(case w.week when 0 then 'Spring' else 'Week '||w.week end) as week,
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
		rc.executePreparedQueryForSelect(q)
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

	def getWaitListTopsForReport(rc: RequestCache, juniorId: Int): List[WaitListTopForReport] = {
		val q = new PreparedQueryForSelect[WaitListTopForReport](Set(MemberRequestCache)) {
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
				  |(case w.week when 0 then 'Spring' else 'Week '||w.week end) as week,
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
		rc.executePreparedQueryForSelect(q)
	}

	case class WaitListForReport(instanceId: Int, typeId: Int, className: String, week: String, dateString: String, timeString: String, wlPosition: Int)
	object WaitListForReport {

		implicit val format = Json.format[WaitListForReport]

		def apply(v: JsValue): WaitListForReport = v.as[WaitListForReport]
	}

	def getWaitListsForReport(rc: RequestCache, juniorId: Int): List[WaitListForReport] = {
		val q = new PreparedQueryForSelect[WaitListForReport](Set(MemberRequestCache)) {
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
				  |(case w.week when 0 then 'Spring' else 'Week '||w.week end) as week,
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
		rc.executePreparedQueryForSelect(q)
	}

	def addSCMIfNotMember(rc: RequestCache, parentPersonId: Int, juniorId: Int): Unit = {
		val orderId = getOrderIdJP(rc, parentPersonId)
		val getCurrentMembership = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				"""
				  |select 1 from active_jp_members where person_id = ?
				  |""".stripMargin
		}
		val hasCurrentMembership = rc.executePreparedQueryForSelect(getCurrentMembership).nonEmpty

		val hasSCMQuery = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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
		val hasSCM = rc.executePreparedQueryForSelect(hasSCMQuery).nonEmpty
		if (!hasCurrentMembership && !hasSCM) {

			val createSCM = new PreparedQueryForInsert(Set(MemberRequestCache)) {
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
			rc.executePreparedQueryForInsert(createSCM)
		}
	}

	def deleteRegistration(rc: RequestCache, parentPersonId: Int, juniorId: Int): ValidationResult = {
		val orderId = getOrderIdJP(rc, parentPersonId)
		val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteSCM)

		val deleteClassReservations = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString)

			override def getQuery: String =
				"""
				  |delete from jp_class_signups where person_id = ? and signup_type = 'P'
				  |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(deleteClassReservations)
		ValidationOk
	}

	def apDeleteReservation(rc: RequestCache, memberID: Int): ValidationResult = {
		val orderId = getOrderIdAP(rc, memberID)
		val deleteSCGP = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_guest_privs where order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteSCGP)

		val deleteSCDW = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(memberID.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_waivers where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteSCDW)

		val deleteSCM = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(memberID.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteSCM)

		val deleteStaggers = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				  |delete from order_staggered_payments where order_id = $orderId and nvl(paid, 'N') <> 'Y'
				  |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(deleteStaggers)

		val deleteUnpaidPaymentIntents = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				  |delete from ORDERS_STRIPE_PAYMENT_INTENTS where order_id = $orderId and nvl(paid,'N') <> 'Y'
				  |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(deleteUnpaidPaymentIntents)

		val removeNumberOfPayments = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				  |update order_numbers set ADDL_STAGGERED_PAYMENTS  = null where order_id = $orderId
				  |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(removeNumberOfPayments)

		ValidationOk
	}

	def canCheckout(rc: RequestCache, parentPersonId: Int, orderId: Int): Boolean = {
		val incompleteItemsQuery = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override val params: List[String] = List(parentPersonId.toString, parentPersonId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select
				  |1
				  |from persons p, shopping_cart_memberships sc
				  |where p.person_id = sc.person_id
				  |and order_id = $orderId
				  |and p.person_id in (select b from acct_links where a = ? union select to_number(?) from dual)
				  |and (sc.ready_to_buy = 'N')
				  |
				  |""".stripMargin
		}

		val hasIncompleteItems = rc.executePreparedQueryForSelect(incompleteItemsQuery).nonEmpty

		val completeItemsQuery = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override val params: List[String] = List(
				parentPersonId.toString,
				parentPersonId.toString,
				orderId.toString,
				parentPersonId.toString,
				parentPersonId.toString,
				orderId.toString,
				orderId.toString,
				parentPersonId.toString,
				orderId.toString,
			)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select
				  |1
				  |from persons p, shopping_cart_memberships sc
				  |where p.person_id = sc.person_id
				  |and order_id = $orderId
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
				  |
				  |union all
				  |select 1 from ap_class_signups
				  |where person_Id = ? and order_id = ? and signup_type = 'P'
				  |""".stripMargin
		}

		val hasCompleteItems = rc.executePreparedQueryForSelect(completeItemsQuery).nonEmpty

		hasCompleteItems && !hasIncompleteItems
	}

	def getSignupNote(rc: RequestCache, juniorId: Int, instanceId: Int): Either[API.ValidationError, Option[String]] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(MemberRequestCache)) {
			override val params: List[String] = List(juniorId.toString, instanceId.toString)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override def getQuery: String =
				"""
				  |select PARENT_SIGNUP_NOTE from jp_class_signups
				  |where person_id = ? and instance_id = ?
				  |""".stripMargin
		}
		val result = rc.executePreparedQueryForSelect(q)
		if (result.length == 1) Right(result.head)
		else Left(ValidationResult.from("Unable to locate signup for this class."))
	}

	def saveSignupNote(rc: RequestCache, juniorId: Int, instanceId: Int, signupNote: Option[String]): ValidationResult = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
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

		val result = rc.executePreparedQueryForUpdateOrDelete(q)
		if (result == 1) ValidationOk
		else ValidationResult.from("Unable to update signup note.")
	}

	def assessDiscounts(rc: RequestCache, orderId: Int): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(MemberRequestCache)) {
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
		rc.executeProcedure(ppc)
	}

	def removeOffseasonWaitlist(rc: RequestCache, juniorId: Int): Unit = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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

		val ids = rc.executePreparedQueryForSelect(q)

		if (ids.nonEmpty) {
			val deleteSignupsQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
				override val params: List[String] = List(juniorId.toString)
				override def getQuery: String =
					s"""
					   |delete from jp_class_signups where person_id = ? and signup_id in (${ids.mkString(",")})
					   |""".stripMargin
			}

			rc.executePreparedQueryForUpdateOrDelete(deleteSignupsQ)

			val deleteWLQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
				override def getQuery: String =
					s"""
					   |delete from jp_class_wl_results wl
					   |where wl.signup_id in (${ids.mkString(",")})
					   |""".stripMargin
			}

			rc.executePreparedQueryForUpdateOrDelete(deleteWLQ)
		}
	}

	def getDiscountsWithAmounts(rc: RequestCache): List[DiscountWithAmount] = {
		val q = new PreparedQueryForSelect[DiscountWithAmount](Set(MemberRequestCache)) {
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
		rc.executePreparedQueryForSelect(q)
	}

	case class DiscountWithAmount (
		discountId: Int,
		instanceId: Int,
		membershipTypeId: Int,
		fullPrice: Double,
		discountAmount: Double
	)

	def getFYExpirationDate(rc: RequestCache, personId: Int): (Int, LocalDate) = {
		val q = new PreparedQueryForSelect[(Int, LocalDate)](Set(MemberRequestCache)) {
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
		val result = rc.executePreparedQueryForSelect(q)
		result.head
	}

	def getAPDiscountEligibilities(rc: RequestCache, personId: Int): (Boolean, Boolean, Boolean, Boolean) = {
		val q = new PreparedQueryForSelect[(Boolean, Boolean, Boolean, Boolean)](Set(MemberRequestCache)) {
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
		val result = rc.executePreparedQueryForSelect(q)
		result.head
	}

	def getDiscountActiveInstanceForDiscount(rc: RequestCache, discountId: Int): Option[Int] = {
		val allowedDiscounts = Set(
			MagicIds.DISCOUNTS.RENEWAL_DISCOUNT_ID,
			MagicIds.DISCOUNTS.YOUTH_DISCOUNT_ID,
			MagicIds.DISCOUNTS.SENIOR_DISCOUNT_ID,
			MagicIds.DISCOUNTS.MGH_DISCOUNT_ID,
			MagicIds.DISCOUNTS.VETERAN_DISCOUNT_ID,
			MagicIds.DISCOUNTS.MA_TEACHERS_ASSN_DISCOUNT_ID,
			MagicIds.DISCOUNTS.STUDENT_DISCOUNT_ID,
		)

		if (!allowedDiscounts.contains(discountId)) None
		else {
			val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(discountId.toString)

				override def getQuery: String =
					"""
					  |select instance_id from discount_active_instances where discount_id = ?
					  |""".stripMargin
			}

			rc.executePreparedQueryForSelect(q).headOption
		}


	}

	/**  @return (staggeredPaymentsAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) */
	def apSelectMemForPurchase(rc: RequestCache, personId: Int, orderId: Int, memTypeId: Int, requestedDiscountInstanceId: Option[Int])(implicit PA: PermissionsAuthority):
	(Boolean, Boolean, Boolean, Boolean) = {
		val allowedTypes = Set(
			MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_TYPE_ID,
			MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_PADDLING_TYPE_ID,
			MagicIds.MEMBERSHIP_TYPES.WICKED_BASIC_30_DAY,
			MagicIds.MEMBERSHIP_TYPES.FULL_30_DAY,
			MagicIds.MEMBERSHIP_TYPES.FULL_60_DAY,
		)
		if (!allowedTypes.contains(memTypeId)) throw new Exception("Invalid membership type selected")
		val existingSCMs = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(personId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |select count(*) from shopping_cart_memberships where person_id = ? and order_id = ?
				  |""".stripMargin
		}
		val count = rc.executePreparedQueryForSelect(existingSCMs).head

		if (count == 0) {
			val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
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
			rc.executePreparedQueryForInsert(insertQ)
		} else {
			val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
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
			rc.executePreparedQueryForUpdateOrDelete(updateQ)
		}

		val now = PA.now()
		val month = now.format(DateTimeFormatter.ofPattern("MM")).toInt

		val paymentPlanAllowed = (month > 9 || month < 5) && MembershipLogic.membershipTypeAllowsStaggeredPayments(memTypeId)

		val gpdwQuery = new PreparedQueryForSelect[(Boolean, Boolean, Boolean)](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Boolean, Boolean, Boolean) =
				(rsw.getBooleanFromChar(1), rsw.getBooleanFromChar(2), rsw.getBooleanFromChar(3))

			override def getQuery: String =
				s"""
				  |select cc_pkg.guest_privs_auto($personId, $memTypeId), cc_pkg.guest_privs_na($personId, $memTypeId), cc_pkg.damage_waiver_auto($personId, $memTypeId) from dual
				  |""".stripMargin
		}
		val (guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) = rc.executePreparedQueryForSelect(gpdwQuery).head

		(paymentPlanAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto)
	}

	def apSetGuestPrivs(rc: RequestCache, personId: Int, orderId: Int, wantIt: Boolean): Unit = {
		val countQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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
		val count = rc.executePreparedQueryForSelect(countQ).head

		if (count == 0 && wantIt) {

			val scmQ = new PreparedQueryForSelect[Option[Int]](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[Int] = rsw.getOptionInt(1)

				override val params: List[String] = List(orderId.toString, personId.toString)

				override def getQuery: String =
					"""
					  |select max(item_id) from shopping_cart_memberships where order_id = ? and person_id = ?
					  |""".stripMargin
			}

			val scmId = rc.executePreparedQueryForSelect(scmQ).headOption.flatten

			lazy val realMemInsertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
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
				(scmId: Int) => new PreparedQueryForInsert(Set(MemberRequestCache)) {
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

			rc.executePreparedQueryForInsert(insertQ)
		} else if (!wantIt) {
			val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
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
			rc.executePreparedQueryForUpdateOrDelete(deleteQ)
		}
	}

	def apSetDamageWaiver(rc: RequestCache, personId: Int, orderId: Int, wantIt: Boolean): Unit = {
		val countQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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
		val count = rc.executePreparedQueryForSelect(countQ).head

		if (count == 0 && wantIt) {
			val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
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

			rc.executePreparedQueryForInsert(insertQ)
		} else if (!wantIt) {
			val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
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
			rc.executePreparedQueryForUpdateOrDelete(deleteQ)
		}
	}

	def addDonationToOrder(rc: RequestCache, orderId: Int, fundId: Int, amount: Double, inMemoryOf: Option[String] = None): ValidationResult = {
		if (amount <= 0) {
			ValidationResult.from("Donation amount must be >= $0.")
		} else {
			val existsQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(orderId.toString, fundId.toString)

				override def getQuery: String =
					"""
					  |select 1 from shopping_cart_donations where order_id = ? and fund_id = ?
					  |""".stripMargin
			}
			val exists = rc.executePreparedQueryForSelect(existsQ).nonEmpty
			if (exists) {
				val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
					override val params: List[String] = List(amount.toString, orderId.toString, fundId.toString)
					override def getQuery: String =
						"""
						  |update shopping_cart_donations
						  |set amount = ?
						  |where order_id = ? and fund_id = ?
						  |""".stripMargin
				}
				rc.executePreparedQueryForUpdateOrDelete(updateQ)
			} else {
				val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
					override val pkName: Option[String] = Some("ITEM_ID")

					override val params: List[String] = List(
						orderId.toString,
						amount.toString,
						fundId.toString,
						inMemoryOf.orNull,
					)

					override def getQuery: String =
						"""
						  |insert into shopping_cart_donations(
						  |      order_id,
						  |      amount,
						  |      fund_id,
						  |      initiative_id,
						  |      in_memory_of
						  |    ) values (
						  |      ?,
						  |      round(?, 2),
						  |      ?,
						  |      16,
						  |      ?
						  |    )
						  |""".stripMargin
				}
				rc.executePreparedQueryForInsert(insertQ)
			}

			updateFirstStaggeredPaymentWithOneTimes(rc, orderId)

			ValidationOk
		}

	}

	def deleteDonationFromOrder(rc: RequestCache, orderId: Int, fundId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override val params: List[String] = List(orderId.toString, fundId.toString)
			override def getQuery: String =
				"""
				  |delete from shopping_cart_donations
				  |where order_id = ? and fund_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)

		updateFirstStaggeredPaymentWithOneTimes(rc, orderId)
	}

	def attemptAddPromoCode(rc: RequestCache, orderId: Int, code: String): Unit = {
		val getDiscountQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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

		val instances = rc.executePreparedQueryForSelect(getDiscountQ)
		if (instances.nonEmpty) {
			val instanceId = instances.head
			val existsQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(orderId.toString, instanceId.toString)

				override def getQuery: String =
					"""
					  |select 1 from orders_promos where order_id = ? and instance_id = ?
					  |""".stripMargin
			}
			if (rc.executePreparedQueryForSelect(existsQ).isEmpty) {
				val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
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
				rc.executePreparedQueryForInsert(insertQ)
			}
		}
	}

	def addGiftCertificateToOrder(rc: RequestCache, gcNumber: Int, gcCode: String, orderId: Int, now: LocalDate): ValidationResult = {
		val validQ = new PreparedQueryForSelect[(Int, LocalDate)](Set(MemberRequestCache)) {
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
		val certs = rc.executePreparedQueryForSelect(validQ)
		if (certs.nonEmpty) {
			val (certId, certExp) = certs.head
			if (now.isAfter(certExp)) {
				ValidationResult.from("That gift certificate has expired.")
			} else {
				val proc = new PreparedProcedureCall[Unit](Set(MemberRequestCache)) {

					override def setInParametersInt: Map[String, Int] = Map(
						"P_CERT_ID" -> certId,
						"p_order_id" -> orderId
					)

					override def getQuery: String = "cc_pkg.apply_gc(?, ?)"

					override def registerOutParameters: Map[String, Int] = Map.empty

					override def getOutResults(cs: CallableStatement): Unit = Unit
				}
				rc.executeProcedure(proc)
				ValidationOk
			}
		} else {
			ValidationResult.from("Gift certificate could not be redeemed; please try again.")
		}
	}

	def unapplyAllGCFromOrder(rc: RequestCache, orderId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_appl_gc where order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	def unapplyGCFromOrder(rc: RequestCache, orderId: Int, certId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(certId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |delete from shopping_cart_appl_gc where cert_id = ? and order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	def apCanClaim(rc: RequestCache, email: String): Either[String, Int] = {
		val proc = new PreparedProcedureCall[Either[String, Int]](Set(PublicRequestCache, BouncerRequestCache)) {
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
		rc.executeProcedure(proc)
	}

	def apDoClaim(rc: RequestCache, personId: Int): Unit = {
		val proc = new PreparedProcedureCall[Unit](Set(BouncerRequestCache)) {
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
		rc.executeProcedure(proc)
	}

	def validateClaimAcctHash(rc: RequestCache, email: String, personId: Int, hash: String): ValidationResult = {
		val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
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
		val exists = rc.executePreparedQueryForSelect(q).nonEmpty
		if (exists) ValidationOk
		else ValidationResult.from("An internal error occurred; if this message persists please contact CBI at 617-523-1038")
	}

	def getAllMembershipPrices(rc: RequestCache): List[(Int, Double)] = {
		val q = new PreparedQueryForSelect[(Int, Double)](Set(PublicRequestCache)) {
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
		rc.executePreparedQueryForSelect(q)
	}

	/**
	 * @return (DW, GP, ApClass)
	 */
	def getConstantPrices(rc: RequestCache): (Double, Double, Double) = {
		val q = new PreparedQueryForSelect[(Double, Double, Double)](Set(PublicRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Double, Double, Double) =
				(rsw.getDouble(1), rsw.getDouble(2), rsw.getDouble(3))

			override def getQuery: String =
				"""
				  |select
				  |global_constant_pkg.get_value_number('DW_PRICE'),
					|global_constant_pkg.get_value_number('GP_PRICE'),
					|global_constant_pkg.get_value_number('AP_CLASS_PRICE')
				  |from dual
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).head
	}

	def getAllDiscounts(rc: RequestCache): List[(Int, Int, Double)] = {
		val q = new PreparedQueryForSelect[(Int, Int, Double)](Set(PublicRequestCache)) {
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
		rc.executePreparedQueryForSelect(q)
	}

	def getApClassTypeAvailabilities(rc: RequestCache, personId: Int): List[ApClassAvailability] = {
		val q = new PreparedQueryForSelect[ApClassAvailability](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ApClassAvailability = ApClassAvailability(
				typeName=rsw.getString(2),
				typeId=rsw.getInt(1),
				availabilityFlag=rsw.getInt(4),
				displayOrder = rsw.getDouble(3),
				noSignup = rsw.getOptionBooleanFromChar(5).getOrElse(false),
				seeTypeError = rsw.getOptionString(6),
				description = rsw.getString(7),
				canTeach = rsw.getBooleanFromChar(8)
			)

			override val params: List[String] = List(personId.toString, personId.toString, personId.toString)

			override def getQuery: String =
				"""
				  |select
				  |type_id,
				  |type_name,
				  |display_order,
				  |ap_class_pkg.get_type_visibility(?, type_id),
				  |t.no_signup,
				  |ap_class_pkg.see_type(?, t.type_id),
				  |t.desc_long,
				  |ap_class_pkg.can_teach_type(?, t.type_id)
				  |from ap_class_types t
				  |order by t.display_order
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}
	case class ApClassAvailability (
		typeId: Int,
		typeName: String,
		availabilityFlag: Int,
		displayOrder: Double,
		noSignup: Boolean,
		seeTypeError: Option[String],
		description: String,
		canTeach: Boolean
	)

	object ApClassAvailability {
		implicit val format = Json.format[ApClassAvailability]

		def apply(v: JsValue): ApClassAvailability = v.as[ApClassAvailability]
	}

	def getApClassesInstructorInfo(rc: RequestCache, personId: Int): List[MemberApClassInstancesInstructorInfoGetResponseSuccessDto] = {
		val instancesQ: PreparedQueryForSelect[MemberApClassInstancesInstructorInfoGetResponseSuccessDto] = new PreparedQueryForSelect[MemberApClassInstancesInstructorInfoGetResponseSuccessDto](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): MemberApClassInstancesInstructorInfoGetResponseSuccessDto = new MemberApClassInstancesInstructorInfoGetResponseSuccessDto(
				instanceId = rsw.getInt(1),
				instructorName = rsw.getOptionString(2),
				signupCt = rsw.getInt(3),
				signupMin = rsw.getOptionInt(4),
				signupMax = rsw.getOptionInt(5),
			)

			override val params: List[String] = List()

			override def getQuery: String =
				"""
				  |select
				  |i.instance_id,
				  |(case when p.person_id is null then null else p.name_first || ' ' || substr(nvl(p.name_last,' '),0,1) end),
				  |(select count(*) from ap_class_signups where instance_id = i.instance_id and signup_type = 'E'),
				  |i.signup_min,
				  |i.signup_max
				  |from ap_class_bookends bk, ap_class_sessions fs, ap_class_instances i
				  |left outer join persons p
				  |on i.instructor_id = p.person_id
				  |where i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				  |and fs.session_datetime > (add_months(util_pkg.get_sysdate, -18))
				  |and fs.session_datetime < (add_months(util_pkg.get_sysdate, 8))
				  |and i.cancelled_datetime is null
				  |and nvl(i.hide_online,'N') <> 'Y'
				  |order by fs.session_datetime
				  |
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(instancesQ, 2000)
	}

	def attemptUnenrollTeachApClass(rc: RequestCache, personId: Int, instanceId: Int): Option[String] = {
		val ppc = new PreparedProcedureCall[String](Set(MemberRequestCache)) {
			//				procedure attempt_set_ap_class_instructor(
			//					i_person_id in number,
			//					i_instance_id in number,
			//					i_override in char,
			//					o_error out varchar2
			//				);
			override def registerOutParameters: Map[String, Int] = Map(
				"o_error" -> java.sql.Types.CHAR
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_instance_id" -> instanceId,

			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error")

			override def getQuery: String = "ap_class_pkg.attempt_unenroll_ap_class_instructor(?, ?, ?)"
		}

		rc.executeProcedure(ppc) match {
			case null => None
			case s: String => Some(s)
		}
	}

	def attemptSignupTeachApClass(rc: RequestCache, personId: Int, instanceId: Int): Option[String] = {
		val ppc = new PreparedProcedureCall[String](Set(MemberRequestCache)) {
//				procedure attempt_set_ap_class_instructor(
//					i_person_id in number,
//					i_instance_id in number,
//					i_override in char,
//					o_error out varchar2
//				);
			override def registerOutParameters: Map[String, Int] = Map(
				"o_error" -> java.sql.Types.CHAR
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_instance_id" -> instanceId,

			)

			override def setInParametersVarchar: Map[String, String] = Map(
				"i_override" -> "N"
			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error")

			override def getQuery: String = "ap_class_pkg.attempt_set_ap_class_instructor(?, ?, ?, ?)"
		}

		rc.executeProcedure(ppc) match {
			case null => None
			case s: String => Some(s)
		}
	}

	def getApClassesForCalendar(rc: RequestCache, personId: Int): List[ApClassInstanceForCalendar] = {
		val instancesQ: PreparedQueryForSelect[ApClassInstanceForCalendar] = new PreparedQueryForSelect[ApClassInstanceForCalendar](Set(MemberRequestCache)) {
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
				instructorId = rsw.getOptionInt(9)
			)

			override val params: List[String] = List(personId.toString, personId.toString, personId.toString)

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
				  |i.price,
				  |i.instructor_id
				  |from ap_class_types t, ap_class_formats f, ap_class_bookends bk, ap_class_sessions fs, ap_class_instances i
				  |left outer join ap_class_signups si
				  |on si.instance_id = i.instance_id and si.person_id = ?
				  |and (si.signup_type is null or si.signup_type not in ('U','A'))
				  |left outer join ap_class_wl_results wlr
				  |on si.signup_id = wlr.signup_id
				  |where i.format_id = f.format_id and f.type_id = t.type_id
				  |and i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				  |and fs.session_datetime > (add_months(util_pkg.get_sysdate, -18))
				  |and fs.session_datetime < (add_months(util_pkg.get_sysdate, 8))
				  |and i.cancelled_datetime is null
				  |and nvl(i.hide_online,'N') <> 'Y'
				  |and ap_class_pkg.actually_see_instance(?, i.instance_id) is null
				  |order by fs.session_datetime
				  |
				  |""".stripMargin
		}

		val instances = rc.executePreparedQueryForSelect(instancesQ, 2000)
		val instanceIDs = instances.map(_.instanceId)
		val sessionsQ = new PreparedQueryForSelect[ApClassSessionForCalendar](Set(MemberRequestCache)) {
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
		val sessions = rc.executePreparedQueryForSelect(sessionsQ, 2000)
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
		price: Double,
		instructorId: Option[Int]
	)

	object ApClassInstanceForCalendar{
		implicit val format = Json.format[ApClassInstanceForCalendar]

		def apply(v: JsValue): ApClassInstanceForCalendar = v.as[ApClassInstanceForCalendar]
	}

	def apClassSignup(rc: RequestCache, personId: Int, instanceId: Int, isWaitlist: Boolean): Option[String] = {
		val ppc = new PreparedProcedureCall[String](Set(MemberRequestCache)) {
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
				"i_order_id" -> getOrderIdAP(rc, personId),
			)


			override def setInParametersVarchar: Map[String, String] = Map(
				"i_signup_type" -> { if (isWaitlist) "W" else "P" },
				"i_do_override" -> "N",
				"i_payment_medium" -> null,
			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error_msg")

			override def getQuery: String = "ap_class_pkg.attempt_signup(?, ?, ?, ?, ?, ?, ?, ?)"
		}

		rc.executeProcedure(ppc) match {
			case null => None
			case s: String => Some(s)
		}
	}

	def apClassUnenroll(rc: RequestCache, personId: Int, instanceId: Int): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(MemberRequestCache)) {
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

		rc.executeProcedure(ppc)
	}

	def getStripeCustomerId(rc: RequestCache, personId: Int): Option[String] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override val params: List[String] = List(personId.toString)
			override def getQuery: String =
				"""
				  |select stripe_customer_id from persons where person_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).head
	}

	def getAllMembershipTypesWithPrices(rc: RequestCache): List[(Int, Option[Currency])]  = {
		val q = new PreparedQueryForSelect[(Int, Option[Currency])](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, Option[Currency]) =
				(rsw.getInt(1), rsw.getOptionDouble(2).map(Currency.dollars))

			override def getQuery: String =
				"""
				  |select membership_type_id, price from membership_types
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}

	/** @return List[(Date, (canRenew, guestPrivsAuto))] */
	def canRenewOnEachDate(rc: RequestCache, personId: Int, membershipTypeId: Int, dates: List[LocalDate]): List[(LocalDate, (Boolean, Boolean))] = {
		type CanRenewOnDate = (LocalDate, (Boolean, Boolean))
		val q = new PreparedQueryForSelect[CanRenewOnDate](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): CanRenewOnDate =
				(rsw.getLocalDate(1), (rsw.getBooleanFromChar(2), rsw.getBooleanFromChar(3)))

			override def getQuery: String = dates.map(d => {
				s"""
				   |select
				   |${GetSQLLiteral(d)},
				   |person_pkg.can_renew($personId, ${Storable.GetSQLLiteral(d)}),
				   |cc_pkg.guest_privs_auto($personId, $membershipTypeId, ${Storable.GetSQLLiteral(d)})
				   |from dual
				   |""".stripMargin
			}).mkString(" UNION ALL ")
		}
		rc.executePreparedQueryForSelect(q)
	}

	/** @return (Option[GPPrice], Option[DWPrice]) */
	def getGPAndDwFromCart(rc: RequestCache, personId: Int, orderId: Int): (Option[Currency], Option[Currency]) = {
		type GpAndDwPrice = (Option[Currency], Option[Currency])
		val ITEM_TYPE_DW = "Damage Waiver"
		val ITEM_TYPE_GP = "Guest Privileges"
		val q = new PreparedQueryForSelect[(String, Currency)](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, Currency) =
				(rsw.getString(1), Currency.dollars(rsw.getDouble(2)))

			override def getQuery: String =
				s"""
				  |select ITEM_TYPE, PRICE from full_cart
				  |where order_id = $orderId
				  |and ITEM_TYPE in ('$ITEM_TYPE_DW', '$ITEM_TYPE_GP')
				  |""".stripMargin
		}
		val rows = rc.executePreparedQueryForSelect(q)

		// Turn the 0-2 rows of GP and DW price into a single object
		rows.foldLeft((None, None): GpAndDwPrice)((result, row) => {
			val (itemType, price) = row
			itemType match {
				case ITEM_TYPE_GP => (Some(price), result._2)
				case ITEM_TYPE_DW => (result._1, Some(price))
				case _ => result
			}
		})
	}

	def getSingleAPSCM(rc: RequestCache, personId: Int, orderId: Int): Option[SCMRecord] = {
		val q = new PreparedQueryForSelect[SCMRecord](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): SCMRecord =
				SCMRecord(
					membershipTypeId = rsw.getInt(1),
					basePriceDollars = rsw.getDouble(2),
					discountId = rsw.getOptionInt(3),
					discountAmtDollars = rsw.getOptionDouble(4),
					addlStaggeredPayments = rsw.getOptionInt(5).getOrElse(0)
				)

			override def getQuery: String =
				s"""
				   |select membership_type_id, price, di.discount_id, discount_amt, o.ADDL_STAGGERED_PAYMENTS
				   |from shopping_cart_memberships scm
				   |inner join order_numbers o on scm.order_id = o.order_id
				   |left outer join discount_instances di
				   |on scm.discount_instance_id = di.instance_id
				   |where scm.order_id = $orderId and scm.person_id = $personId
				   |""".stripMargin
		}
		val scms = rc.executePreparedQueryForSelect(q)
		if (scms.length != 1) None
		else Some(scms.head)
	}

	def getAllJPSCMs(rc: RequestCache, orderId: Int): List[SCMRecord] = {
		val q = new PreparedQueryForSelect[SCMRecord](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): SCMRecord =
				SCMRecord(
					membershipTypeId = rsw.getInt(1),
					basePriceDollars = rsw.getDouble(2),
					discountId = rsw.getOptionInt(3),
					discountAmtDollars = rsw.getOptionDouble(4),
					addlStaggeredPayments = rsw.getOptionInt(5).getOrElse(0)
				)

			override def getQuery: String =
				s"""
				   |select membership_type_id, price, di.discount_id, discount_amt, o.ADDL_STAGGERED_PAYMENTS
				   |from shopping_cart_memberships scm
				   |inner join order_numbers o on scm.order_id = o.order_id
				   |left outer join discount_instances di
				   |on scm.discount_instance_id = di.instance_id
				   |where scm.order_id = $orderId and scm.membership_type_id = 10
				   |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}

	def getPaymentPlansForMembershipInCart(rc: RequestCache, personId: Int, orderId: Int, now: LocalDate): (Option[SCMRecord], List[List[(LocalDate, Currency)]]) = {
		val scm = PortalLogic.getSingleAPSCM(rc, personId, orderId)

		scm match {
			case None => (None, List.empty)
			case Some(scmRecord) => {
				val (membershipTypeId, membershipPrice, discountIdOption, discountAmtOption, addlStaggeredMonths) = SCMRecord.unapply(scmRecord).get
				val (gpPriceOption, dwPriceOption) = PortalLogic.getGPAndDwFromCart(rc, personId, orderId)
				val priceAsCurrency =
					Currency.dollars(membershipPrice) + gpPriceOption.getOrElse(Currency.cents(0)) + dwPriceOption.getOrElse(Currency.cents(0))

				// return a function mapping end date to price
				// for renewals, if the end date is past their "can renew" date,
				// then the price is the full price ie the current price plus the renewal discoutn amt added back on
				// Otherwise its just the price that SCM says it is
				val endDateToPrice = (endDates: List[LocalDate]) => {
					val samePrice: LocalDate => Currency = _ => priceAsCurrency - Currency.dollars(discountAmtOption.getOrElse(0d))

					discountIdOption match {
						case Some(MagicIds.DISCOUNTS.RENEWAL_DISCOUNT_ID) => {
							// map of (proposed payment plan end date) => (can the user still renew on that date)
							val canRenewOnEachDate = PortalLogic.canRenewOnEachDate(rc, personId, membershipTypeId, endDates).toMap

							// return a fn that takes a date and checks it against the map above.
							// If they cant renew, put the discount amt back on the price
							(ld: LocalDate) => {
								val (canRenew, guestPrivsAuto) = canRenewOnEachDate(ld)
								if (canRenew) {
									priceAsCurrency - Currency.dollars(discountAmtOption.getOrElse(0d))
								} else {
									priceAsCurrency
								}
							}
						}
						case _ => samePrice
					}
				}
				(Some(scmRecord), MembershipLogic.calculateAllPaymentSchedules(now, endDateToPrice))
			}
		}
	}

	case class SCMRecord (
		membershipTypeId: Int,
		basePriceDollars: Double,
		discountId: Option[Int],
		discountAmtDollars: Option[Double],
		addlStaggeredPayments: Int
	)

	object SCMRecord {
		implicit val format = Json.format[SCMRecord]
		def apply(v: JsValue): SCMRecord = v.as[SCMRecord]
	}

	def getPaymentAdditionalMonths(rc: RequestCache, orderId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select nvl(ADDL_STAGGERED_PAYMENTS,0) from order_numbers where order_id = $orderId
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).head
	}

	def clearStripeTokensFromOrder(rc: RequestCache, orderId: Int): Unit = {
		val clearQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override val params: List[String] = List(orderId.toString)

			override def getQuery: String = "update stripe_tokens set active = 'N' where order_id = ?"
		}
		rc.executePreparedQueryForUpdateOrDelete(clearQ)
	}

	/**
	 * Delete unpaid staggered payment records on the supplied orderId.  Throw if there are any payment records remaining afterward
	 * @param pb
	 * @param orderId
	 * @return paymentIntentRowID from the deleted rows (if any)
	 */
	def deleteStaggeredPaymentsWithEmptyAssertion(rc: RequestCache, orderId: Int): Option[Int] = {
		val getintentRowID = new PreparedQueryForSelect[Option[Int]](Set(MemberRequestCache, ApexRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[Int] = rsw.getOptionInt(1)

			override def getQuery: String =
				s"""
				  |select max(PAYMENT_INTENT_ROW_ID) from order_staggered_payments
				  |where order_id = $orderId
				  |""".stripMargin
		}
		val piRowId = rc.executePreparedQueryForSelect(getintentRowID).head

		val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				  |delete from ORDER_STAGGERED_PAYMENTS
				  |where order_id = $orderId and nvl(paid, 'N') <> 'Y' and paid_close_id is null and redeemed_close_id is null
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteQ)
		val selectQ = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = 1

			override def getQuery: String =
				s"""
				  |select 1 from ORDER_STAGGERED_PAYMENTS where order_id = $orderId
				  |""".stripMargin
		}
		val recordsLeft = rc.executePreparedQueryForSelect(selectQ).length
		if (recordsLeft > 0) {
			throw new Exception (s"deleteStaggeredPaymentsWithEmptyAssertion called on orderId $orderId which has paid records")
		}
		piRowId
	}

	def writeOrderStaggeredPaymentsAP(rc: RequestCache, now: LocalDate, personId: Int, orderId: Int, numberAdditionalPayments: Int): List[(LocalDate, Currency)] = {
		// Clear existing records.  Throw if there are paid records
		val piRowIdMaybe = deleteStaggeredPaymentsWithEmptyAssertion(rc, orderId)

		val typeIsOk, dropDiscount = PortalLogic.getSingleAPSCM(rc, personId, orderId) match {
			case Some(scm) => MembershipLogic.membershipTypeAllowsStaggeredPayments(scm.membershipTypeId)
			case None => true
		}

		if (typeIsOk) {
			val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
				override def getQuery: String =
					s"""
					   |update order_numbers
					   |set ADDL_STAGGERED_PAYMENTS = $numberAdditionalPayments
					   |where order_id = $orderId
					   |""".stripMargin
			}

			rc.executePreparedQueryForUpdateOrDelete(updateQ)

			PortalLogic.assessDiscounts(rc, orderId)

			if (numberAdditionalPayments > 0) {
				val futurePayments: List[(LocalDate, Currency)] = getPaymentPlansForMembershipInCart(rc, personId, orderId, now)._2.find(_.size == numberAdditionalPayments+1).get

				val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
					override val pkName: Option[String] = None

					override val preparedParamsBatch: List[List[PreparedValue]] = futurePayments.zipWithIndex.map(t => {
						val ((payDate, payAmt), index) = t
						val piRowId: String = if(index == 0) piRowIdMaybe.map(_.toString).orNull else null
						val values: List[PreparedValue] = List(
							orderId,
							(index + 1).toString,
							payDate,
							payAmt.cents.toString,
							GetSQLLiteralPrepared("N"),
							piRowId
						)
						values
					})

					override def getQuery: String =
						s"""
						   |insert into ORDER_STAGGERED_PAYMENTS (order_id, seq, expected_payment_date, raw_amount_in_cents, paid, addl_amount_in_cents, PAYMENT_INTENT_ROW_ID)
						   |values (?, ?, ?, ?, ?, 0, ?)
						   |""".stripMargin
				}
				rc.executePreparedQueryForInsert(insertQ)

				val toAdd = updateFirstStaggeredPaymentWithOneTimes(rc, orderId)
				val newFirstPayment = (futurePayments.head._1, futurePayments.head._2 + toAdd)
				newFirstPayment :: futurePayments.tail
			} else List.empty
		} else {
			val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
				override def getQuery: String =
					s"""
					   |update order_numbers
					   |set ADDL_STAGGERED_PAYMENTS = 0
					   |where order_id = $orderId
					   |""".stripMargin
			}

			rc.executePreparedQueryForUpdateOrDelete(updateQ)
			List.empty
		}
	}

	def writeOrderStaggeredPaymentsJP(rc: RequestCache, now: LocalDate, orderId: Int, doingStaggered: Boolean): List[(LocalDate, Currency)] = {
		// Clear existing records.  Throw if there are paid records
		val piRowIdMaybe = deleteStaggeredPaymentsWithEmptyAssertion(rc, orderId)

		val schedule =
			if(doingStaggered) getJPAvailablePaymentSchedule(rc, orderId, now, false)
			else List.empty

		val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				   |update order_numbers
				   |set ADDL_STAGGERED_PAYMENTS = ${schedule.length}
				   |where order_id = $orderId
				   |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(updateQ)

		PortalLogic.assessDiscounts(rc, orderId)

		if (schedule.length > 0) {
			val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
				override val pkName: Option[String] = None

				override val preparedParamsBatch: List[List[PreparedValue]] = schedule.zipWithIndex.map(t => {
					val ((payDate, payAmt), index) = t
					val piRowId: String = if(index == 0) piRowIdMaybe.map(_.toString).orNull else null
					val values: List[PreparedValue] = List(
						orderId,
						(index + 1).toString,
						payDate,
						payAmt.cents.toString,
						GetSQLLiteralPrepared("N"),
						piRowId
					)
					values
				})

				override def getQuery: String =
					s"""
					   |insert into ORDER_STAGGERED_PAYMENTS (order_id, seq, expected_payment_date, raw_amount_in_cents, paid, addl_amount_in_cents, PAYMENT_INTENT_ROW_ID)
					   |values (?, ?, ?, ?, ?, 0, ?)
					   |""".stripMargin
			}
			rc.executePreparedQueryForInsert(insertQ)

			val toAdd = updateFirstStaggeredPaymentWithOneTimes(rc, orderId)
			val newFirstPayment = (schedule.head._1, schedule.head._2 + toAdd)
			newFirstPayment :: schedule.tail
		} else List.empty
	}

	def calculateFirstStaggeredPaymentOneTimes(rc: RequestCache, orderId: Int): Currency = {
		// Add everything in the cart that is not staggered, to the first payment
		val cartQ = new PreparedQueryForSelect[Currency](Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Currency = Currency.dollars(rsw.getOptionDouble(1).getOrElse(0d))

			override def getQuery: String =
				s"""
				   |select nvl(sum(nvl(price,0)),0) from full_cart
				   |where order_id = $orderId
				   |and item_type not in ('Membership', 'Damage Waiver', 'Discount', 'Gift Certificate Redeemed', 'Guest Privileges')
				   |""".stripMargin
		}
		rc.executePreparedQueryForSelect(cartQ).head
	}


	def updateFirstStaggeredPaymentWithOneTimes(rc: RequestCache, orderId: Int): Currency = {
		val oneTimePrice = calculateFirstStaggeredPaymentOneTimes(rc, orderId)
		
		val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
			override def getQuery: String =
				s"""
				   |update ORDER_STAGGERED_PAYMENTS
				   |set addl_amount_in_cents = ${oneTimePrice.cents}
				   |where order_id = $orderId and seq = 1
				   |""".stripMargin
		}

		rc.executePreparedQueryForUpdateOrDelete(updateQ)

		oneTimePrice
	}

	def getOrCreatePaymentIntent(rc: RequestCache, stripe: StripeIOController, personId: Int, orderId: Int, totalInCents: Int): Future[Option[PaymentIntent]] = {
		val closeId = rc.executePreparedQueryForSelect(new GetCurrentOnlineClose).head.closeId

		val q = new PreparedQueryForSelect[(String, Int, Int)](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, Int, Int) = (rsw.getString(1), rsw.getInt(2), rsw.getInt(3))

			override def getQuery: String =
				s"""
				  |select PAYMENT_INTENT_ID, paid_close_id, row_id from ORDERS_STRIPE_PAYMENT_INTENTS where order_id = $orderId and nvl(paid, 'N') <> 'Y'
				  |""".stripMargin
		}

		val intentIdsAndCloseIDs = rc.executePreparedQueryForSelect(q)

		if (intentIdsAndCloseIDs.isEmpty) {
			createPaymentIntent(rc, stripe, personId, orderId, closeId)
		} else if (intentIdsAndCloseIDs.length == 1) {
			val (intentId, closeId, rowId) = intentIdsAndCloseIDs.head
			stripe.getPaymentIntent(intentId).flatMap({
				case s: NetSuccess[PaymentIntent, _] => {
					val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
						override def getQuery: String =
							s"""
							   |update order_staggered_payments set
							   |PAYMENT_INTENT_ROW_ID = $rowId
							   |where stagger_id in (select stagger_id from staggered_payments_ready where order_id = $orderId)
							   |""".stripMargin
					}
					rc.executePreparedQueryForUpdateOrDelete(updateQ)
					val pi = s.successObject
					if (pi.amount != totalInCents || pi.metadata.closeId.get.toInt != closeId) {
						stripe.updatePaymentIntentWithTotal(pi.id, totalInCents, closeId).map(_ => {
							val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
								override def getQuery: String =
									s"""
									   |update ORDERS_STRIPE_PAYMENT_INTENTS set
									   |AMOUNT_IN_CENTS = $totalInCents,
									   |paid_close_id = $closeId
									   |where order_id = $orderId
									   |and nvl(paid, 'N') <> 'Y'
									   |""".stripMargin
							}
							rc.executePreparedQueryForUpdateOrDelete(updateQ)
							Some(pi)
						})
					} else {
						Future(Some(pi))
					}
				}
				case _ => throw new Exception("Failed to get PaymentIntent")
			})
		} else {
			throw new Exception("Multiple unpaid paymentIntents found for order " + orderId)
		}
	}

	private def getStaggeredPaymentsReady(rc: RequestCache, orderId: Int): List[(Int, Currency)] = {
		val getStaggeredPaymentsQ = new PreparedQueryForSelect[(Int, Currency)](Set(MemberRequestCache, ApexRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Int, Currency) = (
				rsw.getInt(1),
				Currency.cents(rsw.getOptionInt(2).getOrElse(0) + rsw.getOptionInt(3).getOrElse(0))
			)

			override def getQuery: String =
				s"""
				   |select STAGGER_ID, RAW_AMOUNT_IN_CENTS, ADDL_AMOUNT_IN_CENTS
				   |from STAGGERED_PAYMENTS_READY where order_id = $orderId
				   |""".stripMargin
		}
		rc.executePreparedQueryForSelect(getStaggeredPaymentsQ)
	}

	private def createPaymentIntent(rc: RequestCache, stripe: StripeIOController, personId: Int, orderId: Int, closeId: Int): Future[Option[PaymentIntent]] = {
		val appAlias = getAppAliasForOrderId(rc, orderId)
		val staggeredPayments = if (appAlias == MagicIds.ORDER_NUMBER_APP_ALIAS.AUTO_DONATE || appAlias == MagicIds.ORDER_NUMBER_APP_ALIAS.DONATE) {
			List.empty
		} else {
			getStaggeredPaymentsReady(rc, orderId)
		}
		val totalInCents = {
			if (appAlias == MagicIds.ORDER_NUMBER_APP_ALIAS.AUTO_DONATE || appAlias == MagicIds.ORDER_NUMBER_APP_ALIAS.DONATE) {
				getOrderTotalCents(rc, orderId)
			} else {
				staggeredPayments.foldLeft(0)((amt, payment) => amt + payment._2.cents)
			}
		}

		if (totalInCents == 0) Future(None)
		else {
			stripe.createPaymentIntent(orderId, None, totalInCents, PortalLogic.getStripeCustomerId(rc, personId).get, closeId).flatMap({
				case intentSuccess: NetSuccess[PaymentIntent, _] => {
					val pi = intentSuccess.successObject

					val createPI = new PreparedQueryForInsert(Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
						override val pkName: Option[String] = Some("ROW_ID")

						override val params: List[String] = List(pi.id)

						override def getQuery: String =
							s"""
							   |insert into ORDERS_STRIPE_PAYMENT_INTENTS (order_id, payment_intent_id, amount_in_cents, paid, PAID_CLOSE_ID)
							   |values ($orderId, ?, $totalInCents, 'N', $closeId)
							   |""".stripMargin
					}

					val rowId = rc.executePreparedQueryForInsert(createPI).get

					if (appAlias != MagicIds.ORDER_NUMBER_APP_ALIAS.AUTO_DONATE && appAlias != MagicIds.ORDER_NUMBER_APP_ALIAS.DONATE) {
						val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache)) {
							override def getQuery: String =
								s"""
								   |update order_staggered_payments set
								   |PAYMENT_INTENT_ROW_ID = $rowId
								   |where order_id = $orderId
								   |and stagger_id in (${staggeredPayments.map(_._1).mkString(", ")})
								   |""".stripMargin
						}
						rc.executePreparedQueryForUpdateOrDelete(updateQ)
					}

					val customerId = PortalLogic.getStripeCustomerId(rc, personId).get

					// If there is already a payment method on the customer, stick it on this payment intent
					stripe.getCustomerDefaultPaymentMethod(customerId).flatMap({
						case paymentMethodSuccess: NetSuccess[Option[PaymentMethod], _] => paymentMethodSuccess.successObject match {
							case Some(pm: PaymentMethod) => {
								stripe.updatePaymentIntentWithPaymentMethod(pi.id, pm.id).map(_ => Some(pi))
							}
							case None => Future(Some(pi))
						}
						case _ => throw new Exception("Failed to get payment method")
					})
				}
				case _ => throw new Exception("Failed to create PaymentIntent")
			})
		}
	}

	def getJPAvailablePaymentSchedule(rc: RequestCache, orderId: Int, now: LocalDate, addOneTimes: Boolean): List[(LocalDate, Currency)] = {
		val month = now.format(DateTimeFormatter.ofPattern("MM")).toInt

		val jpscms = getAllJPSCMs(rc, orderId)
		val total = jpscms.foldLeft(0d)((agg, scm) => agg + scm.basePriceDollars - scm.discountAmtDollars.getOrElse(0d))

		if (month >= 5 && month <= 10) List.empty
		else if (total < 100) List.empty // per niko/fiona, dont offer under $100 carts
		else {
			val addlMonths = if (month > 10) {
				4 + (13 - month)
			} else {
				5-month
			}

			val rawSchedule = MembershipLogic.calculatePaymentSchedule(now, _ => Currency.dollars(total), addlMonths)

			// Add onetimes to the first payment
			if (addOneTimes) {
				val onetimes = calculateFirstStaggeredPaymentOneTimes(rc, orderId)
				(rawSchedule.head._1, rawSchedule.head._2 + onetimes) :: rawSchedule.tail
			} else {
				rawSchedule
			}
		}
	}

	def getOpenStaggeredOrderForPerson(rc: RequestCache, personId: Int)(implicit PA: PermissionsAuthority): Option[Int] = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache, StaffRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select order_id
				  |from pending_memberships
				  |where person_id = $personId
				  |""".stripMargin
		}

		val openOrders = rc.executePreparedQueryForSelect(q)
		if (openOrders.size > 1) {
			PA.logger.error("Found " + openOrders.size + " open orders for personID " + personId)
			Some(openOrders.max)
		} else openOrders.headOption
	}

	case class StaggeredOrderPayment(orderId: Int, staggerId: Int, amountCents: Int, expectedDate: LocalDate, paid: Boolean, failedCron: Boolean)
	object StaggeredOrderPayment{
		implicit val format = Json.format[StaggeredOrderPayment]
		def apply(v: JsValue): StaggeredOrderPayment = v.as[StaggeredOrderPayment]
	}

	def getStaggeredOrderStatus(rc: RequestCache, orderId: Int): List[StaggeredOrderPayment] = {
		val q = new PreparedQueryForSelect[StaggeredOrderPayment](Set(MemberRequestCache, StaffRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): StaggeredOrderPayment =
				StaggeredOrderPayment(rsw.getInt(1), rsw.getInt(2), rsw.getInt(3) + rsw.getOptionInt(4).getOrElse(0),
					rsw.getLocalDate(5), rsw.getBooleanFromChar(6), rsw.getOptionBooleanFromChar(7).getOrElse(false))

			override def getQuery: String =
				s"""
				  |select order_id, stagger_id, raw_amount_in_Cents, addl_amount_in_cents, expected_payment_date, paid, failed_cron
				  |from order_staggered_payments where order_id = $orderId
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q)
	}

	def callDoStaggeredPaymentCron(rc: RequestCache, personId: Int, orderId: Int)(implicit PA: PermissionsAuthority): ValidationResult = {
		// execute ready payments
		val ppc = new PreparedProcedureCall[(Boolean, String)](Set(MemberRequestCache, StaffRequestCache)) {
			//procedure do_staggered_payment_cron(
			//  i_person_id in number,
			//  i_order_id in number,
			//  o_success out char,
			//  o_error_msg out varchar2
			//)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_order_id" -> orderId,
			)

			override def registerOutParameters: Map[String, Int] = Map(
				"o_success" -> java.sql.Types.CHAR,
				"o_error_msg" -> java.sql.Types.VARCHAR,
			)

			override def getOutResults(cs: CallableStatement): (Boolean, String) = {
				val success = cs.getString("o_success").substring(0, 1)
				println("success was '" + success + "'")
				println("success len " + success.length)
				println("equals Y ?  " + (success == "Y"))
				(success == "Y", cs.getString("o_error_msg"))
			}

			override def getQuery: String = "api_pkg.do_staggered_payment_cron(?,?,?,?)"
		}

		val (wasSuccess, errorMessage) = rc.executeProcedure(ppc)
		print("finished ppc call: " + wasSuccess + ":  " + errorMessage)
		if (wasSuccess) ValidationOk
		else ValidationResult.from(errorMessage)
	}

	def finishOpenOrder(rc: RequestCache, personId: Int, orderId: Int): ValidationResult = {
		// set all staggered payments to ready
		val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, StaffRequestCache)) {
			override def getQuery: String =
				s"""
				  |update order_staggered_payments
				  |set expected_payment_date = util_pkg.get_sysdate
				  |where order_id = $orderId and paid = 'N'
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(updateQ)

		callDoStaggeredPaymentCron(rc, personId, orderId)
	}

	def retryFailedPayments(rc: RequestCache, personId: Int, orderId: Int): ValidationResult = {
		println("about to retry...")
		if (getStaggeredPaymentsReady(rc, orderId).nonEmpty) {
			println("calling the cron ppc")
			callDoStaggeredPaymentCron(rc, personId, orderId)
		} else ValidationOk
	}

	def updateAllPaymentIntentsWithNewMethod(rc: RequestCache, personId: Int, methodId: String, stripe: StripeIOController): Future[List[ServiceRequestResult[_, _]]] = {
		val q = new PreparedQueryForSelect[String](Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				s"""
				  |select pi.payment_intent_id
				  |from order_numbers o, order_staggered_payments osp, ORDERS_STRIPE_PAYMENT_INTENTS pi
				  |where o.order_id = osp.order_id and osp.PAYMENT_INTENT_ROW_ID = pi.ROW_ID
				  |and osp.paid = 'N' and o.person_id = $personId
				  |""".stripMargin
		}
		val intents = rc.executePreparedQueryForSelect(q)
		Future.sequence(intents.map(i => {
			stripe.updatePaymentIntentWithPaymentMethod(i, methodId)
		}))
	}

	case class PurchaseGiftCertShape(
		valueInCents: Int,
		purchasePriceCents: Int,
		purchaserNameFirst: Option[String],
		purchaserNameLast: Option[String],
		purchaserEmail: Option[String],
		recipientNameFirst: Option[String],
		recipientNameLast: Option[String],
		recipientEmail: Option[String],
		addr1: Option[String],
		addr2: Option[String],
		city: Option[String],
		state: Option[String],
		zip: Option[String],
		deliveryMethod: String,
		whoseAddress: Option[String],
		whoseEmail: Option[String]
	)

	object PurchaseGiftCertShape {
		implicit val format = Json.format[PurchaseGiftCertShape]

		def apply(v: JsValue): PurchaseGiftCertShape = v.as[PurchaseGiftCertShape]
	}

	val dummyEmptyPurchaseGC = PurchaseGiftCertShape(
		valueInCents = 0,
		purchasePriceCents = 0,
		recipientEmail = None,
		recipientNameFirst = None,
		recipientNameLast = None,
		addr1 = None,
		addr2 = None,
		city = None,
		state = None,
		zip = None,
		deliveryMethod = "X",
		whoseAddress = None,
		whoseEmail = None,
		purchaserNameFirst = None,
		purchaserNameLast = None,
		purchaserEmail = None,
	)

	def setGiftCertPurchase(rc: RequestCache, personId: Int, orderId: Int, gc: PurchaseGiftCertShape): Unit = {
		val countQ = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String =
				s"""
				  |select 1 from shopping_cart_gcs where order_id = $orderId
				  |""".stripMargin
		}

		val ct = rc.executePreparedQueryForSelect(countQ).size

		if (ct > 1) {
			throw new Exception("Order had " + ct + " shopping cart GCs on it")
		} else if (ct == 1) {
			val deleteQ = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
				override def getQuery: String = s"delete from shopping_cart_gcs where order_id = $orderId"
			}
			rc.executePreparedQueryForUpdateOrDelete(deleteQ)
		}

		val insertQ = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
			override val pkName: Option[String] = None

			override val params: List[String] = List(
				gc.recipientEmail.orNull,
				gc.recipientNameFirst.orNull,
				gc.recipientNameLast.orNull,
				gc.addr1.orNull,
				gc.addr2.orNull,
				gc.city.orNull,
				gc.state.orNull,
				gc.zip.orNull,
				gc.deliveryMethod,
				gc.whoseAddress.orNull,
				gc.whoseEmail.orNull,
			)

			override def getQuery: String =
				s"""
				  |insert into shopping_cart_gcs(
				  |    value,
				  |    purchase_price,
				  |    order_id,
				  |    recipient_email,
				  |    ready_to_buy,
				  |    recipient_name_first,
				  |    recipient_name_last,
				  |    recipient_addr_1,
				  |    recipient_addr_2,
				  |    recipient_city,
				  |    recipient_state,
				  |    recipient_zip,
				  |    delivery_method,
				  |    whose_addr,
				  |    whose_email
				  |  ) values (
				  |    ${gc.purchasePriceCents / 100},
				  |    ${gc.purchasePriceCents / 100},
				  |    $orderId,
				  |    ?,
				  |    'Y',
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?,
				  |    ?
				  |  )
				  |""".stripMargin
		}

		rc.executePreparedQueryForInsert(insertQ)
	}

	def getGiftCertPurchase(rc: RequestCache, orderId: Int): PurchaseGiftCertShape = {
		val q = new PreparedQueryForSelect[PurchaseGiftCertShape](Set(ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): PurchaseGiftCertShape = PurchaseGiftCertShape(
				valueInCents = (rsw.getDouble(1) * 100).toInt,
				purchasePriceCents = (rsw.getDouble(2) * 100).toInt,
				recipientEmail = rsw.getOptionString(3),
				recipientNameFirst = rsw.getOptionString(4),
				recipientNameLast = rsw.getOptionString(5),
				addr1 = rsw.getOptionString(6),
				addr2 = rsw.getOptionString(7),
				city = rsw.getOptionString(8),
				state = rsw.getOptionString(9),
				zip = rsw.getOptionString(10),
				deliveryMethod = rsw.getString(11),
				whoseAddress = rsw.getOptionString(12),
				whoseEmail = rsw.getOptionString(13),
				purchaserNameFirst = rsw.getOptionString(14),
				purchaserNameLast = rsw.getOptionString(15),
				purchaserEmail = rsw.getOptionString(16),
			)

			override def getQuery: String =
				s"""
				  |select
				  |    value,
				  |    purchase_price,
				  |    recipient_email,
				  |    recipient_name_first,
				  |    recipient_name_last,
				  |    recipient_addr_1,
				  |    recipient_addr_2,
				  |    recipient_city,
				  |    recipient_state,
				  |    recipient_zip,
				  |    delivery_method,
				  |    whose_addr,
				  |    whose_email,
				  |    p.name_first,
				  |    p.name_last,
				  |    p.email
				  |    from shopping_Cart_gcs scgc, order_numbers o, persons p
				  |    where scgc.order_id = o.order_id and o.person_id = p.person_id
				  |    and o.order_id = $orderId
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).headOption match {
			case Some(x) => x
			case None => dummyEmptyPurchaseGC
		}
	}

	def getAuthedPersonInfo(rc: RequestCache, protoPersonId: Int): (Option[String], Option[String], Option[String], Option[Int]) = {
		val q = new PreparedQueryForSelect[(Option[String], Option[String], Option[String], Option[Int])](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Option[String], Option[String], Option[String], Option[Int]) =
				(rsw.getOptionString(1), rsw.getOptionString(2), rsw.getOptionString(3), rsw.getOptionInt(4))

			override def getQuery: String =
				s"""
				  |select name_first, name_last, email, has_authed_as
				  |from persons
				  |where person_id = $protoPersonId
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).headOption match {
			case Some(ret) => ret
			case None => (None, None, None, None)
		}
	}

	def mergeRecords(rc: RequestCache, mergeInto: Int, mergeFrom: Int): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(ProtoPersonRequestCache)) {
//			create or replace PROCEDURE "MERGE_RECORDS"
//			(   p_merge_into IN NUMBER,
//				p_merge_from IN NUMBER   )
			override def setInParametersInt: Map[String, Int] = Map(
				"p_merge_into"-> mergeInto,
				"p_merge_from"-> mergeFrom,
			)
			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = Unit

			override def getQuery: String = "merge_records(?, ?)"
		}

		rc.executeProcedure(ppc)
	}

	def promoteProtoPerson(rc: RequestCache, personId: Int): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
			override def getQuery: String =
				s"""
				  |update persons set proto_state = '${MagicIds.PERSONS_PROTO_STATE.WAS_PROTO}'
				  |where person_id = $personId
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	def createGuestCard(rc: RequestCache, personId: Int): (Int, Int, String) = {
		val getCardNumber = new HardcodedQueryForSelect[Int](Set(ProtoPersonRequestCache, KioskRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

			override def getQuery: String = "select GUEST_CARD_SEQ.nextval from dual"
		}

		val cardNumber = rc.executePreparedQueryForSelect(getCardNumber).head

		val getClose = new HardcodedQueryForSelect[Int](Set(ProtoPersonRequestCache, KioskRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

			override def getQuery: String = "select c.close_id from fo_closes c, current_closes cur where close_id = inperson_close"
		}

		val closeId = rc.executePreparedQueryForSelect(getClose).head

		val nonce = scala.util.Random.alphanumeric.take(8).mkString

		val createCard = new PreparedQueryForInsert(Set(ProtoPersonRequestCache, KioskRequestCache)) {
			override def getQuery: String =
				"""
				  |insert into persons_cards
				  |(person_id, issue_date, card_num, temp, active, close_id, paid, nonce) values
				  | (?, sysdate, ?, 'N', 'Y', ?, 'N', ?)
							""".stripMargin

			override val params: List[String] = List(
				personId.toString,
				cardNumber.toString,
				closeId.toString,
				nonce
			)
			override val pkName: Option[String] = Some("assign_id")
		}

		(cardNumber, rc.executePreparedQueryForInsert(createCard).get.toInt, nonce)
	}

	def getTicketBarcodeBase64(rc: RequestCache, cardNumber: Int, nonce: String): Option[BufferedImage] = {
		val q = new PreparedQueryForSelect[Option[String]](Set(PublicRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[String] = rsw.getOptionString(1)

			override val preparedParams: List[PreparedValue] = List(cardNumber)

			override def getQuery: String =
				"""
				  |select nonce from persons_cards where card_num = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).head.flatMap(n => {
			if (n == nonce) Some(BarcodeFactory.getImage(cardNumber.toString))
			else None
		})
	}

	def ticketHTML(cardNumber: Int, nonce: String, name: String, forRental: Boolean): String = {
		val imgUrl = if (forRental) "https://portal.community-boating.org/images/rental-ticket.png" else "https://portal.community-boating.org/images/guest-ticket.png"
		val noun = if (forRental) "Rental" else "Guest"
		val sb = new StringBuilder()
		sb.append("<div id=\"printbox\" style=\"padding: 40px; width: 220px; border: 2px solid black;\">")
		sb.append("<img src=\"" + imgUrl + "\" alt=\"Community Boating " + noun + " Ticket\" width=\"150px\" style=\"padding-left: 30px\"></img>")
		sb.append("<img src=\"$API_URL$/api/ap/guest-ticket-barcode?cardNumber=" + cardNumber + "&nonce=" + nonce + "\" alt=\"Barcode Error, Please See Front Office\" width=\"150PX\" style=\"padding-top: 10px; padding-left:30px\" />")
		sb.append("<h3 style=\"text-align: center\">")
		sb.append(name)
		sb.append("</h3>")
		if (forRental) {
			sb.append("<p>Please return this ticket to the front office and tell them you have completed registration online and would like to pay.</p>")
		} else {
			sb.append("<p>Please bring this card with you to the dockhouse when you come sailing.</p>")
		}
		sb.append("</div>")
		sb.toString()
	}

	def sendTicketEmail(rc: RequestCache, email: String, html: String, forRental: Boolean): Unit = {
		val ppc = new PreparedProcedureCall[Unit](Set(ProtoPersonRequestCache)) {
			override def setInParametersVarchar: Map[String, String] = Map(
				"p_email" -> email,
				"p_ticket_html" -> html,
				"p_for_rental" -> ( if (forRental) "Y" else "N" )
			)

			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = Unit

			override def getQuery: String = "email_pkg.ap_guest_ticket(?, ?, ?)"
		}
		rc.executeProcedure(ppc)
	}

	def getApAvailableVoucherCount(rc: RequestCache, personId: Int): Int = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(personId.toString)

			override def getQuery: String =
				"""
				  |select count(*) from ap_class_vouchers
				  |where person_id = ?
				  |and signup_id is null and void_close_id is null
				  |and season = util_pkg.get_current_season
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q).head
	}

	case class RecurringDonation(fundId: Int, amountInCents: Int)
	object RecurringDonation {
		implicit val format = Json.format[RecurringDonation]
		def apply(v: JsValue): RecurringDonation = v.as[RecurringDonation]
	}

	def getRecurringDonations(rc: RequestCache, personId: Int, onlyNonEmbryonic: Boolean = false): List[RecurringDonation] = {
		val q = new PreparedQueryForSelect[RecurringDonation](Set(MemberRequestCache, ApexRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): RecurringDonation = RecurringDonation(
				fundId = rsw.getInt(1),
				amountInCents = rsw.getInt(2)
			)

			override def getQuery: String =
				s"""
				  |select fund_id, amount_in_cents, embryonic from persons_recurring_donations where person_id = $personId
				  |${if (onlyNonEmbryonic) " and embryonic = 'N'" else ""}
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}

	def setRecurringDonations(rc: RequestCache, personId: Int, donations: List[RecurringDonation], embryonic: Boolean): Unit = {
		val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override def getQuery: String = s"delete from persons_recurring_donations where person_id = $personId"
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteQ)

		// Won't ever be more than 4-5 of these so I'm not too worried about speed
		val insertQs = donations.map(d => new PreparedQueryForInsert(Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override val pkName: Option[String] = Some("ROW_ID")

			override def getQuery: String =
				s"""
				  |insert into persons_recurring_donations (person_id, fund_id, amount_in_cents, embryonic)
				  |values (${personId}, ${d.fundId}, ${d.amountInCents}, ${GetSQLLiteral(embryonic)})
				  |""".stripMargin
		})

		insertQs.foreach(rc.executePreparedQueryForInsert)

		if (donations.isEmpty) {
			val update = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
				override def getQuery: String = s"update persons set NEXT_RECURRING_DONATION = null where person_id = $personId"
			}
			rc.executePreparedQueryForUpdateOrDelete(update)
		}
	}

	def getShoppingCartDonationsAsRecurringRecords(rc: RequestCache, orderId: Int): List[RecurringDonation] = {
		val q = new PreparedQueryForSelect[RecurringDonation](Set(ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): RecurringDonation = RecurringDonation(
				fundId = rsw.getInt(1),
				amountInCents = (100 * rsw.getDouble(2)).toInt
			)

			override def getQuery: String = s"select fund_id, amount from shopping_cart_donations where order_id = $orderId"
		}
		rc.executePreparedQueryForSelect(q)
	}

	case class SavedCardOrPaymentMethodData(
		last4: String,
		expMonth: Int,
		expYear: Int,
		zip: Option[String]
	)

	object SavedCardOrPaymentMethodData {
		implicit val format = Json.format[SavedCardOrPaymentMethodData]
		def apply(v: JsValue): SavedCardOrPaymentMethodData = v.as[SavedCardOrPaymentMethodData]
	}

	def reconstituteAutoDonateOrder(rc: RequestCache, personId: Int, orderId: Int): Unit = {
		// clear existing SCDs
		val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache)) {
			override def getQuery: String = s"delete from shopping_cart_donations where order_id = $orderId"
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteQ)

		// put in new SCDs
		val requestedDonations = getRecurringDonations(rc, personId)
		requestedDonations.foreach(d => addDonationToOrder(rc, orderId, d.fundId, d.amountInCents / 100, None))
	}

	def getAppAliasForOrderId(rc: RequestCache, orderId: Int): String = {
		val q = new PreparedQueryForSelect[String](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getOptionString(1).getOrElse(MagicIds.ORDER_NUMBER_APP_ALIAS.SHARED)

			override def getQuery: String = s"select app_alias from order_numbers where order_id = $orderId"
		}
		rc.executePreparedQueryForSelect(q).head
	}

	def setUsePaymentIntent(rc: RequestCache, orderId: Int, usePaymentIntent: Boolean): Unit = {
		val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ProtoPersonRequestCache)) {
			override val params: List[String] = List(
				if(usePaymentIntent) "Y" else "N",
				orderId.toString
			)
			override def getQuery: String =
				"""
				  |update order_numbers set use_payment_intent = ? where order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(updateQ)
	}

	def getUsePaymentIntentFromOrderTable(rc: RequestCache, orderId: Int): Option[Boolean] = {
		val q = new PreparedQueryForSelect[Option[Boolean]](Set(MemberRequestCache, ProtoPersonRequestCache, ApexRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[Boolean] = rsw.getOptionBooleanFromChar(1)

			override def getQuery: String = s"select use_payment_intent from order_numbers where order_id = $orderId"
		}
		rc.executePreparedQueryForSelect(q).head
	}

	def setUsePaymentIntentDonationStandalone(rc: RequestCache, stripe: StripeIOController, personId: Int, orderId: Int, doRecurring: Boolean): Future[Unit] = {
		val createCustomerIdFuture = {
			if (doRecurring) {
				PortalLogic.getStripeCustomerId(rc, personId) match {
					case None => stripe.createStripeCustomerFromPerson(rc, personId)
					case Some(_) => Future()
				}
			} else {
				Future()
			}
		}

		createCustomerIdFuture.map(_ => {
			PortalLogic.setUsePaymentIntent(rc, orderId, doRecurring)
		})
	}

	def getNotifications(rc: RequestCache, personid: Int): PersonAllNotificationsDto = {
		val q = new PreparedQueryForSelect[PersonNotificationDbRecord](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): PersonNotificationDbRecord = PersonNotificationDbRecord(
				notificationEvent = rsw.getString(1),
				notificationMethod = rsw.getString(2)
			)
			override def getQuery: String = s"select NOTIFICATION_EVENT, NOTIFICATION_METHOD from PERSONS_NOTIFICATION_PREFERENCES where person_id = $personid"
		}
		val dbRecords = rc.executePreparedQueryForSelect(q)

		PersonNotification.PersonAllNotificationsDto.mapRecordsToDto(dbRecords)
	}

	def putNotifications(rc: RequestCache, personId: Int, notifications: PersonAllNotificationsDto): PersonAllNotificationsDto = {
		val records = PersonNotification.PersonAllNotificationsDto.mapDtoToRecords(notifications)
		val deleteQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String = s"delete from PERSONS_NOTIFICATION_PREFERENCES where person_id = $personId"
		}
		rc.executePreparedQueryForUpdateOrDelete(deleteQ)
		records.foreach(rec => {
			val insertQ = new PreparedQueryForInsert(Set(MemberRequestCache)) {
				override val pkName: Option[String] = Some("PREF_ID")

				override val params: List[String] = List(
					rec.notificationEvent,
					rec.notificationMethod
				)

				override def getQuery: String =
					s"""
					  |insert into PERSONS_NOTIFICATION_PREFERENCES (PERSON_ID, NOTIFICATION_EVENT, NOTIFICATION_METHOD)
					  |values ($personId, ?, ?)
					  |""".stripMargin
			}
			rc.executePreparedQueryForInsert(insertQ)
		})

		notifications
	}
}


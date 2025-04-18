package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete, PreparedValue}
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{BouncerRequestCache, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

object TransferOrderOwnership {

	def transferOrderDonations(rc: RequestCache, orderId: Int, personId: Option[Int]): Unit = {
		rc.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List()

			override def getQuery: String =
				s"""
							|update shopping_cart_donations
							|set person_id = ${personId.getOrElse("null")}
							|where orderId = $orderId
							|""".stripMargin
		})
	}

	def transferOrder(rc: RequestCache, orderId: Int, newOrderId: Int): Unit = {

		rc.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List()

			override def getQuery: String =
				s"""
							|update shopping_cart_donations
							|set order_id = $newOrderId
							|where orderId = $orderId
							|""".stripMargin
		})

		rc.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List()

			override def getQuery: String =
				s"""
							|update shopping_cart_gcs
							|set order_id = $newOrderId
							|where orderId = $orderId
							|""".stripMargin
		})

	}
}

class ProveMember @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, memberRC => {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, protoRC => {
				val authedPersonId = memberRC.getAuthedPersonId
				println("WHAT SOMETHING HAPPEN")
				println("Proto RC Username: " + protoRC.userName)
				println("Authed person: " + protoRC.getAuthedPersonId)
				println("Member RC Username: " + memberRC.userName)
				println("Authed Member Person Id: " + authedPersonId)

				val personQ = new PreparedQueryForSelect[(Option[String], Option[String], Option[String])](Set(MemberRequestCache)) {
					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Option[String], Option[String], Option[String]) =
						(rsw.getOptionString(1), rsw.getOptionString(2), rsw.getOptionString(3))

					override def getParams: List[PreparedValue] = List(authedPersonId)

					override def getQuery: String =
						"""
							|select name_first, name_last, email
							|from persons where person_id = ? and pw_hash is not null
							|""".stripMargin
				}
				val (nameFirst, nameLast, email) = memberRC.executePreparedQueryForSelect(personQ).head
				val protoPersonId = PortalLogic.persistStandalonePurchaser(protoRC, protoRC.userName, protoRC.getAuthedPersonId, nameFirst, nameLast, email, authedAsOverride = Some(authedPersonId))
				val orderIdDonateProto = PortalLogic.getOrderId(protoRC, protoPersonId, MagicIds.ORDER_NUMBER_APP_ALIAS.DONATE)
				TransferOrderOwnership.transferOrderDonations(memberRC, orderIdDonateProto, Some(memberRC.getAuthedPersonId))

				val updateProtoQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
					override val params: List[String] = List(
						authedPersonId.toString,
						nameFirst.orNull,
						nameLast.orNull,
						email.orNull,
					)

					override def getQuery: String =
						s"""
							|update persons
							|set has_authed_as = ?,
							|name_first = ?,
							|name_last = ?,
							|email = ?
							|where person_id = $protoPersonId
							|""".stripMargin
				}

				memberRC.executePreparedQueryForUpdateOrDelete(updateProtoQ)
				Future(Ok("true"))
			})
		})
	}

	def detach()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			rc.getAuthedPersonId match {
				case Some(personId) => {
					val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
						override def getQuery: String =
							s"""
							  |update persons set has_authed_as = null,
								|email=null
							  |where person_id = $personId
							  |""".stripMargin
					}
					rc.executePreparedQueryForUpdateOrDelete(q)
					val orderIdDonateMember = PortalLogic.getOrderId(rc, personId, MagicIds.ORDER_NUMBER_APP_ALIAS.DONATE)
					TransferOrderOwnership.transferOrderDonations(rc, orderIdDonateMember, Some(personId))
				}
				case None =>
			}
			Future(Ok("success"))
		})
	}

	case class ProveMemberPostShape(username: String)

	object ProveMemberPostShape{
		implicit val format = Json.format[ProveMemberPostShape]

		def apply(v: JsValue): ProveMemberPostShape = v.as[ProveMemberPostShape]
	}
}

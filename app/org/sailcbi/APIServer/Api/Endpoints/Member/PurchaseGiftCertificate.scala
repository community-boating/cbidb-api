package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{EmailUtil, ParsedRequest}
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PurchaseGiftCertificate @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def set()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PurchaseGiftCertShape.apply)(parsed => {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val personId = rc.getAuthedPersonId().get
				val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.GC)

				runValidations(parsed) match {
					case ValidationOk => Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
				}


			})
		})
	}

//	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
//		val parsedRequest = ParsedRequest(request)
//		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PurchaseGiftCertShape.apply)(parsed => {
//			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
//				val personId = rc.getAuthedPersonId().get
//				val orderId = PortalLogic.getOrderId(rc, personId, "Donate")
//
//				PortalLogic.deleteDonationFromOrder(rc, orderId, parsed.fundId)
//
//				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
//			})
//		})
//	}

	private def runValidations(parsed: PurchaseGiftCertShape): ValidationResult = {
		val unconditionals = List(
			ValidationResult.checkBlank(parsed.purchaserNameFirst, "Purchaser First Name"),
			ValidationResult.checkBlank(parsed.purchaserNameLast, "Purchaser Last Name"),
			ValidationResult.checkBlank(parsed.purchaserEmail, "Purchaser Email"),
			ValidationResult.checkBlank(parsed.recipientNameFirst, "Recipient First Name"),
			ValidationResult.checkBlank(parsed.recipientNameLast, "Recipient Last Name"),
			ValidationResult.inline(parsed.deliveryMethod)(
				deliveryMethod => List("Email", "Mail", "Pickup").contains(deliveryMethod),
				"An internal error occurred."
			),
			ValidationResult.inline(parsed.purchaserEmail)(
				purchaserEmail => purchaserEmail.getOrElse("").isEmpty || EmailUtil.regex.findFirstIn(purchaserEmail.getOrElse("")).isDefined,
				"Purchaser email is not valid."
			),
		)

		val ifEmail = List((
			true,
			ValidationResult.inline(parsed.whoseEmail)(
				_.isDefined,
				"Please specify where to email the certificate."
			)
		), (
			parsed.whoseEmail.getOrElse("") == "Recipient",
			ValidationResult.checkBlank(parsed.recipientEmail, "Recipient Email"),
		), (
			parsed.whoseEmail.getOrElse("") == "Recipient",
			ValidationResult.inline(parsed.recipientEmail)(
				recipientEmail => recipientEmail.getOrElse("").isEmpty || EmailUtil.regex.findFirstIn(recipientEmail.getOrElse("")).isDefined,
				"Recipient email is not valid."
			)
		))

		val ifMail = List(
			ValidationResult.checkBlankCustom(parsed.whoseAddress, "Please indicate whose address is specified (purchaser or recipient)"),
			ValidationResult.checkBlank(parsed.addr1, "Address"),
			ValidationResult.checkBlank(parsed.city, "City"),
			ValidationResult.checkBlank(parsed.state, "State"),
			ValidationResult.checkBlank(parsed.zip, "Zip"),
			ValidationResult.inline(parsed.zip)(zip => "^[0-9]{5}(-[0-9]{4})?$".r.findFirstIn(zip.getOrElse("")).isDefined, "Zip code is an invalid format."),
		)

		val conditionals = parsed.deliveryMethod match {
			case "Email" => ifEmail.filter(_._1).map(_._2)
			case "Mail" => ifMail
			case _ => List.empty
		}

		ValidationResult.combine(unconditionals ::: conditionals)
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
}
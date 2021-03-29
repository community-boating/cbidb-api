package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.Util.EmailUtil
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.{PurchaseGiftCertShape, dummyEmptyPurchaseGC}
import org.sailcbi.APIServer.UserTypes.ProtoPersonRequestCache
import play.api.libs.json.{JsBoolean, JsObject, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PurchaseGiftCertificate @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def set()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		println("here we go")
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PurchaseGiftCertShape.apply)(parsed => {
			println("successfully parsed post body")
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				println("got RC")
				runValidations(parsed) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						val personId = PortalLogic.persistStandalonePurchaser(
							rc,
							rc.userName,
							rc.getAuthedPersonId,
							parsed.purchaserNameFirst,
							parsed.purchaserNameLast,
							parsed.purchaserEmail
						)

						val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.GC)

						println(personId)
						println(orderId)
						println(parsed)

						PortalLogic.setGiftCertPurchase(rc, personId, orderId, parsed)

						Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					}
				}
			})
		})
	}

	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		if (parsedRequest.cookies.get(ProtoPersonRequestCache.COOKIE_NAME).isDefined) {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				rc.getAuthedPersonId match {
					case None => Future(Ok(Json.toJson(dummyEmptyPurchaseGC)))
					case Some(personId) => {
						val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.GC)

						Future(Ok(Json.toJson(PortalLogic.getGiftCertPurchase(rc, orderId))))
					}
				}
			})
		} else {
			Future(Ok(Json.toJson(dummyEmptyPurchaseGC)))
		}
	}

	private def runValidations(parsed: PurchaseGiftCertShape): ValidationResult = {
		val unconditionals = List(
			ValidationResult.checkBlank(parsed.purchaserNameFirst, "Purchaser First Name"),
			ValidationResult.checkBlank(parsed.purchaserNameLast, "Purchaser Last Name"),
			ValidationResult.checkBlank(parsed.purchaserEmail, "Purchaser Email"),
			ValidationResult.checkBlank(parsed.recipientNameFirst, "Recipient First Name"),
			ValidationResult.checkBlank(parsed.recipientNameLast, "Recipient Last Name"),
			ValidationResult.inline(parsed.deliveryMethod)(
				deliveryMethod => List("E", "M", "P").contains(deliveryMethod),
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
			parsed.whoseEmail.getOrElse("") == "R",
			ValidationResult.checkBlank(parsed.recipientEmail, "Recipient Email"),
		), (
			parsed.whoseEmail.getOrElse("") == "R",
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
			case "E" => ifEmail.filter(_._1).map(_._2)
			case "M" => ifMail
			case _ => List.empty
		}

		ValidationResult.combine(unconditionals ::: conditionals)
	}


}
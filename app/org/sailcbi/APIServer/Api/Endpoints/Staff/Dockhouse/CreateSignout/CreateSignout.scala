package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.BitVector
import org.sailcbi.APIServer.Entities.EntityDefinitions.Signout
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateSignout @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	val ERROR_RATING_INSUFFICIENT = CreateSignoutSingleError(code = "rating-insufficient", overridable = true, message = "The skipper does not have the required rating.")
	val ERROR_RATING_UNUSABLE = CreateSignoutSingleError(code = "rating-unusable", overridable = true, message = "The skipper's rating is frozen.")
	val ERROR_SIGNOUT_HOLD = CreateSignoutSingleError(code = "signout-hold", overridable = false, message = "The skipper is currently on signout hold.")
	val ERROR_DISCOUNT_FREEZE = CreateSignoutSingleError(code = "discount-freeze", overridable = false, message = "The skipper's membership is frozen pending discount verification.")
	val ERROR_BAD_REQUEST = CreateSignoutSingleError(code = "bad-request", overridable = false, message = "Invalid signout request.")
	val ERROR_PROGRAM_CLOSED = CreateSignoutSingleError(code = "program-closed", overridable = false, message = "The program is closed.")
	val ERROR_NO_MEMBERSHIP = CreateSignoutSingleError(code = "no-membership", overridable = false, message = "The skipper does not have an active membership.")
	val ERROR_NO_RATING_TRIVIAL = CreateSignoutSingleError(code = "no-rating-trivial", overridable = true, message = "The skipper does not have the required rating; read them the rules and resubmit to grant.")
	val ERROR_INVALID_PURPOSE = CreateSignoutSingleError(code = "invalid-purpose", overridable = false, message = "May not race at the same time as a test/class.")
	val ERROR_UNPASSABLE_TEST = CreateSignoutSingleError(code = "unpassable-test", overridable = true, message = "The skipper's membership does not permit them to hold the rating they are trying to test for.")
	val ERROR_UNPERFORMABLE_TEST = CreateSignoutSingleError(code = "unperformable-test", overridable = true, message = "The weather conditions do not permit this test.")
	val ERROR_INVALID_OCCUPANCY = CreateSignoutSingleError(code = "invalid-occupancy", overridable = false, message = "Invalid occupancy for that boat type.")
	val ERROR_ALREADY_SAILING = CreateSignoutSingleError(code = "already-sailing", overridable = true, message = "At least one person in that boat is already on the water")
	val ERROR_GUESTS_INVALID = CreateSignoutSingleError(code = "guests-invalid", overridable = false, message = "The skipper is not permitted to take guests with them.")
	val ERROR_CREW_INVALID = CreateSignoutSingleError(code = "crew-invalid", overridable = true, message = "Invalid crew.")

	val errorList = List(
		ERROR_RATING_INSUFFICIENT,
		ERROR_RATING_UNUSABLE,
		ERROR_SIGNOUT_HOLD,
		ERROR_DISCOUNT_FREEZE,
		ERROR_BAD_REQUEST,
		ERROR_PROGRAM_CLOSED,
		ERROR_NO_MEMBERSHIP,
		ERROR_NO_RATING_TRIVIAL,
		ERROR_INVALID_PURPOSE,
		ERROR_UNPASSABLE_TEST,
		ERROR_UNPERFORMABLE_TEST,
		ERROR_INVALID_OCCUPANCY,
		ERROR_ALREADY_SAILING,
		ERROR_GUESTS_INVALID,
		ERROR_CREW_INVALID,
	)

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, CreateSignoutRequest.apply)(parsed => {
				println(parsed)
				attemptSignout(parsed).fold(
					e => Future(BadRequest(e.asJsObject)),
					s => Future(Ok(Json.toJson(s)))
				)
			})
		})
	})

	def attemptSignout(req: CreateSignoutRequest): Either[CreateSignoutError, Signout] = {
		if (!req.isRacing) collectErrors(List.empty)
		req.sailNumber match {
			case Some(s) => collectErrors(mapSailNumToArtificalErrors(s))
			case None => collectErrors(List.empty)
		}
	}

	private def collectErrors(errors: List[CreateSignoutSingleError]): Either[CreateSignoutError, Signout] = {
		if (errors.nonEmpty) {
			Left(CreateSignoutError("validation-error","validation error", errors))
		} else {
			val s = new Signout
			s.values.signoutId.update(-1)
			Right(s)
		}
	}

	private def mapSailNumToArtificalErrors(sailNumber: String): List[CreateSignoutSingleError] = {
		try {
			val sailNumberInt = sailNumber.toInt
			errorList.zipWithIndex.filter(t => BitVector.testBit(sailNumberInt, t._2)).map(_._1)
		} catch {
			case _: NumberFormatException => List.empty
		}
	}

	case class CreateSignoutCrewRequest(
	   personId: Int,
	   cardNumber: String,
	   testRatingId: Option[Int]
	)

	case class CreateSignoutRequest(
		skipperPersonId: Int,
		skipperCardNumber: String,
		skipperTestRatingId: Option[Int],
		boatId: Int,
		sailNumber: Option[String],
		hullNumber: Option[String],
		classSessionId: Option[Int],
		isRacing: Boolean,
		dockmasterOverride: Boolean,
		didInformKayakRules: Boolean,
		signoutCrew: List[CreateSignoutCrewRequest]
	) {
		def toJson: JsValue = {
			implicit val successFormat = CreateSignoutRequest.format
			Json.toJson(this)
		}
	}

	object CreateSignoutRequest {
		implicit val crewFormat = Json.format[CreateSignoutCrewRequest]
		implicit val format = Json.format[CreateSignoutRequest]
		def apply(v: JsValue): CreateSignoutRequest = v.as[CreateSignoutRequest]
	}
}


package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.API.ResultError
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Signout
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateSignout @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	val ERROR_RATING_INSUFFICIENT =	CreateSignoutSingleError(code = "rating-insufficient",	overridable = true,		message = "The skipper does not have the required rating.")
	val ERROR_RATING_UNUSABLE =		CreateSignoutSingleError(code = "rating-unusable",		overridable = true,		message = "The skipper's rating is frozen.")
	val ERROR_SIGNOUT_HOLD =		CreateSignoutSingleError(code = "signout-hold",			overridable = false,	message = "The skipper is currently on signout hold.")
	val ERROR_DISCOUNT_FREEZE =		CreateSignoutSingleError(code = "discount-freeze",		overridable = false,	message = "The skipper's membership is frozen pending discount verification.")
	val ERROR_BAD_REQUEST =			CreateSignoutSingleError(code = "bad-request",			overridable = false,	message = "Invalid signout request.")
	val ERROR_PROGRAM_CLOSED =		CreateSignoutSingleError(code = "program-closed",		overridable = false,	message = "The program is closed.")
	val ERROR_NO_MEMBERSHIP =		CreateSignoutSingleError(code = "no-membership",		overridable = false,	message = "The skipper does not have an active membership.")
	val ERROR_NO_RATING_TRIVIAL =	CreateSignoutSingleError(code = "no-rating-trivial",	overridable = true,		message = "The skipper does not have the required rating; read them the rules and resubmit to grant.")
	val ERROR_INVALID_PURPOSE =		CreateSignoutSingleError(code = "invalid-purpose",		overridable = false,	message = "May not race at the same time as a test/class.")
	val ERROR_UNPASSABLE_TEST =		CreateSignoutSingleError(code = "unpassable-test",		overridable = true,		message = "The skipper's membership does not permit them to hold the rating they are trying to test for.")
	val ERROR_UNPERFORMABLE_TEST =	CreateSignoutSingleError(code = "unperformable-test",	overridable = true,		message = "The weather conditions do not permit this test.")
	val ERROR_INVALID_OCCUPANCY =	CreateSignoutSingleError(code = "invalid-occupancy",	overridable = false,	message = "Invalid occupancy for that boat type.")
	val ERROR_ALREADY_SAILING =		CreateSignoutSingleError(code = "already-sailing",		overridable = true,		message = "At least one person in that boat is already on the water")
	val ERROR_GUESTS_INVALID =		CreateSignoutSingleError(code = "guests-invalid",		overridable = false,	message = "The skipper is not permitted to take guests with them.")
	val ERROR_CREW_INVALID =		CreateSignoutSingleError(code = "crew-invalid",			overridable = true,		message = "Invalid crew.")

	def get(cardNumber: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			Future(Ok("hi"))
		})
	})

	def attemptSignout(req: CreateSignoutRequest): Either[CreateSignoutError, Signout] = {
		val ret = new Signout
		ret.values.sailNumber.update(Some("23"))
		Right(ret)
	}

	private def sailNumToError(sailNumber: String): List[CreateSignoutError] = {
		val errorList = List(

		)
		try {
			val sailNumberInt = sailNumber.toInt
		} catch {
			case _: NumberFormatException => List.empty
		}
	}

	case class CreateSignoutRequest(
		skipperPersonId: Int,
		skipperCardNumber: String,
		skipperTestRatingId: Option[Int],
		boatId: Int,
		sailNumber: Option[String],
		hullNumber: Option[String],
		classSessionid: Option[Int],
		isRacing: Boolean,
		dockmasterOverride: Boolean,
		didInformKayakRules: Boolean,
		signoutCrew: List[CreateSignoutCrewRequest]
	) {
		def toJson: JsValue = {
			implicit val crewFormat = Json.format[CreateSignoutCrewRequest]
			implicit val successFormat = Json.format[CreateSignoutRequest]
			Json.toJson(this)
		}
	}

	case class CreateSignoutCrewRequest(
		personId: Int,
		cardNumber: String,
		testRatingId: Option[Int]
	)
}

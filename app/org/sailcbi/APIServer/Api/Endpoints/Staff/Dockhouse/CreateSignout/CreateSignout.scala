package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignout.{StaffDockhouseCreateSignoutPostRequestDto, StaffDockhouseCreateSignoutPostRequestDto_SignoutCrew, StaffDockhouseCreateSignoutPostResponseSuccessDto, StaffDockhouseCreateSignoutPostResponseSuccessDto_Crew, StaffDockhouseCreateSignoutPostResponseSuccessDto_Tests}
import org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic.{CreateSignoutLogic, CreateSignoutRequest}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateSignout @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StaffDockhouseCreateSignoutPostRequestDto.apply)(parsed => {
				println(parsed)
				CreateSignoutLogic.attemptSignout(rc, parsed).fold(
					e => Future(BadRequest(e.asJsObject)),
					s => Future(Ok(Json.toJson(new StaffDockhouseCreateSignoutPostResponseSuccessDto(
						signoutId = s.values.signoutId.get,
						personId = s.values.personId.get,
						programId = s.values.programId.get,
						boatId = s.values.boatId.get,
						signoutType = s.values.signoutType.get,
						cardNum = s.values.cardNum.get,
						sailNumber = s.values.sailNumber.get,
						hullNumber = s.values.hullNumber.get,
						testRatingId = s.values.testRatingId.get,
						testResult = None,
						isQueued = s.values.isQueued.get,
						signoutDatetime = s.values.signoutDatetime.get.map(_.toString),
						$$crew = s.references.crew.get.toList.map(c => new StaffDockhouseCreateSignoutPostResponseSuccessDto_Crew(
							signoutId = c.values.signoutId.get,
							personId = c.values.personId.get,
							cardNum = c.values.cardNum.get,
							startActive = c.values.startActive.get.map(_.toString)
						)),
						$$tests = s.references.tests.peek.map(_.toList.map(t => new StaffDockhouseCreateSignoutPostResponseSuccessDto_Tests(
							signoutId = t.values.signoutId.get,
							personId = t.values.personId.get,
							ratingId = t.values.ratingId.get
						))).getOrElse(List.empty)
					))))
				)
			})
		})
	})
}


package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignout.{StaffDockhouseCreateSignoutPostRequestDto, StaffDockhouseCreateSignoutPostRequestDto_SignoutCrew, StaffDockhouseCreateSignoutPostResponseSuccessDto, StaffDockhouseCreateSignoutPostResponseSuccessDto_Crew, StaffDockhouseCreateSignoutPostResponseSuccessDto_Tests}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignoutMultiple.{StaffDockhouseCreateSignoutMultiplePostRequestDto, StaffDockhouseCreateSignoutMultiplePostRequestDto_Signouts, StaffDockhouseCreateSignoutMultiplePostRequestDto_Signouts_SignoutCrew}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Signout
import org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic.{CreateSignoutError, CreateSignoutLogic}
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
				CreateSignoutLogic.attemptSignout(rc, parsed).fold(
					e => Future(BadRequest(e.asJsObject)),
					s => Future(Ok(Json.toJson(mapSignoutToOutputDto(s))))
				)
			})
		})
	})

	def postMany()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StaffDockhouseCreateSignoutMultiplePostRequestDto.apply)(parsed => {
				// try each signout, rollback if any return an error
				val result = rc.withTransaction(() => {
					parsed.signouts.foldLeft(Right(List.empty).asInstanceOf[Either[CreateSignoutError, List[Signout]]])((e, s) => e match {
						case Left(e) => Left(e) // if the last signout returned an error, just keep passing it through
						case Right(l) => CreateSignoutLogic.attemptSignout(rc, multipleDtoToSingleDtoSignout(s)) match {
							case Left(e) => Left(e)
							case Right(s) => Right(s :: l)
						}
					})
				})

				result match {
					case Right(l) => Future(Ok(Json.toJson(l.map(mapSignoutToOutputDto))))
					case Left(e) => Future(BadRequest(e.asJsObject))
				}
			})
		})
	})

	private def mapSignoutToOutputDto(s: Signout): StaffDockhouseCreateSignoutPostResponseSuccessDto = new StaffDockhouseCreateSignoutPostResponseSuccessDto(
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
	)

	private def multipleDtoToSingleDtoSignout(s: StaffDockhouseCreateSignoutMultiplePostRequestDto_Signouts): StaffDockhouseCreateSignoutPostRequestDto
		= new StaffDockhouseCreateSignoutPostRequestDto(
			skipperPersonId = s.skipperPersonId,
			programId = s.programId,
			skipperCardNumber = s.skipperCardNumber,
			skipperTestRatingId = s.skipperTestRatingId,
			boatId = s.boatId,
			sailNumber = s.sailNumber,
			hullNumber = s.hullNumber,
			classSessionId = s.classSessionId,
			isRacing = s.isRacing,
			dockmasterOverride = s.dockmasterOverride,
			didInformKayakRules = s.didInformKayakRules,
			signoutCrew = s.signoutCrew.map(multipleDtoToSingleDtoCrew)
		)

	private def multipleDtoToSingleDtoCrew(c: StaffDockhouseCreateSignoutMultiplePostRequestDto_Signouts_SignoutCrew): StaffDockhouseCreateSignoutPostRequestDto_SignoutCrew
		= new StaffDockhouseCreateSignoutPostRequestDto_SignoutCrew(
			personId = c.personId,
			cardNumber = c.cardNumber,
			testRatingId = c.testRatingId
		)
}


package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignout.DtoStaffDockhouseCreateSignoutPostRequest
import org.sailcbi.APIServer.Entities.dto.GuidedSailTimeSlotRangeDTO
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Logic.ApClassLogic
import org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic.CreateSignoutLogic
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, StaffRequestCache}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController, Results}

import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.Console.println
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ApGuidedSailController @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def getTimeSlotsForDay(forDate: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
			val personId = rc.getAuthedPersonId
      val forDateLocalTry = Try(LocalDate.parse(forDate, DateUtil.DATE_FORMATTER))
      forDateLocalTry match {
        case Success(forDateLocal) =>
          val slots = ApClassLogic.getApGuidedSailTimeSlotsForDay(rc, forDateLocal)
          Future(Ok(Json.toJson(slots)))
        case Failure(exception) =>
          Future(BadRequest(Json.obj("error" -> "Failed to parse date")))
      }
		})
	}
  def getTimeSlotsAndCurrent(forYear: Int, forMonth: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
      val currentMonthDatetime = LocalDate.now().withYear(forYear).withMonth(forMonth)
      val slots = ApClassLogic.getApGuidedSailTimeSlots(rc, currentMonthDatetime)
      println("OrderIdIs" + PortalLogic.getOrderIdAP(rc, rc.getAuthedPersonId))
      Future(Ok(Json.toJson(slots)))
    })
  }

}
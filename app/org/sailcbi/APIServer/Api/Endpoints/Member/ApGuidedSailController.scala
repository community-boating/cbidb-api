package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.dto.GuidedSailCreateInstanceResult
import org.sailcbi.APIServer.Logic.ApClassLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApGuidedSailController @Inject()(implicit exec: ExecutionContext) extends InjectedController {
  def getTimeSlots(forYear: Int, forMonth: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
      val currentMonthDate = PA.now().toLocalDate.withYear(forYear).withMonth(forMonth)
      val slots = ApClassLogic.getApGuidedSailTimeSlots(rc, currentMonthDate, PA.now())
      Future(Ok(Json.toJson(slots)))
    })
  }

  def getCurrentInstances()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
      val currentInstances = ApClassLogic.getApGuidedSailInstancesCurrent(rc, rc.getAuthedPersonId)
      Future(Ok(Json.toJson(currentInstances)))
    })
  }

  def createInstance(timeslot: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
      val results = ApClassLogic.apGuidedSailCreateInstance(rc, LocalDateTime.parse(timeslot, DateUtil.DATE_TIME_FORMATTER), rc.getAuthedPersonId)
      Future(Ok(Json.toJson(GuidedSailCreateInstanceResult(results._1.map(f => f._1), results._1.map(f => f._2), results._2))))
    })
  }

  def cancelInstance(instanceId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, block = rc => {
      val result = ApClassLogic.apGuidedSailCancelInstance(rc, instanceId, rc.getAuthedPersonId)
      Future(Ok(Json.toJson(result.getOrElse("OK"))))
    })
  }
}
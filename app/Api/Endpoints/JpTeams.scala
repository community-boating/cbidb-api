package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.{ApiRequest}
import CbiUtil.Profiler
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class JpTeams @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get() = Action.async {
    val request = new JpTeamsRequest()
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class JpTeamsRequest extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "jp-teams"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = Future {
      val profiler = new Profiler

      val teams: List[JpTeam] = pb.getObjectsByFilters(
        JpTeam,
        List.empty,
        100
      )

      val points: List[JpTeamEventPoints] = pb.getObjectsByFilters(
        JpTeamEventPoints,
        List.empty,
        1000
      )

      profiler.lap("did all the databasing")

      // There's probably a functional way to do this
      var teamsToPoints: Map[Int, Int] = Map()
      teams.foreach(t => {
        teamsToPoints += (t.teamId -> 0)
      })
      points.foreach(p => {
        teamsToPoints.get(p.teamId) match {
          case Some(x) => teamsToPoints += (p.teamId -> (x + p.points))
          case None => throw new Exception("Found points for nonexistant team " + p.teamId)
        }
      })

      val result: List[(String, Int)] = teamsToPoints.toIndexedSeq.toList.map(e => {
        val teamName: String = teams.filter(_.teamId == e._1).head.teamName
        (teamName, e._2)
      })


      val sessionsJsArray: JsArray = JsArray(result.map(r => {
        JsArray(IndexedSeq(
          JsString(r._1),
          JsNumber(r._2)
        ))
      }))

      val metaData = JsArray(Seq(
        JsObject(Map("name" -> JsString("TEAM_NAME"))),
        JsObject(Map("name" -> JsString("POINTS")))
      ))

      val data = JsObject(Map(
        "rows" -> sessionsJsArray,
        "metaData" -> metaData
      ))
      data
    }
  }
}

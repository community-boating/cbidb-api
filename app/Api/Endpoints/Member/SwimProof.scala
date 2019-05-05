package Api.Endpoints.Member

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class SwimProof @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(personId: Int): Action[AnyContent] = Action { request =>
    val parsedRequest = ParsedRequest(request)
    val rc: RequestCache = PermissionsAuthority.getRequestCacheMember(None, parsedRequest, Some(personId))._2.get
    val pb: PersistenceBroker = rc.pb
    val cb: CacheBroker = rc.cb

    val select = new PreparedQueryForSelect[SwimProofShape](Set(MemberUserType)) {
      override def mapResultSetRowToCaseObject(rs: ResultSet): SwimProofShape =
        SwimProofShape(
          personId,
          rs.getOptionInt(1)
        )

      override def getQuery: String =
        s"""
           |select
           |swim_proof
           |from persons where person_id = ?
        """.stripMargin

      override val params: List[String] = List(personId.toString)
    }

    val resultObj = pb.executePreparedQueryForSelect(select).head
    val resultJson: JsValue = Json.toJson(resultObj)
    Ok(resultJson)
  }

  def post() = Action { request =>
    try {
      val parsedRequest = ParsedRequest(request)
      val juniorId: Option[Int] = request.body.asJson.map(json => json("personId").toString().toInt)
      val rc: RequestCache = PermissionsAuthority.getRequestCacheMember(None, parsedRequest, juniorId)._2.get
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb
      val data = request.body.asJson
      data match {
        case None => {
          println("no body")
          new Status(400)("no body")
        }
        case Some(v: JsValue) => {
          val parsed = SwimProofShape.apply(v)
          println(parsed)


          val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
            override def getQuery: String =
              s"""
                 |update persons set
                 |swim_proof = ?
                 |where person_id = ?
              """.stripMargin

            override val params: List[String] = List(
              parsed.swimProofId.map(_.toString).orNull,
              parsed.personId.toString
            )
          }

          pb.executePreparedQueryForUpdateOrDelete(updateQuery)

          Ok("done")
        }
        case Some(v) => {
          println("wut dis " + v)
          Ok("wat")
        }
      }
    } catch {
      case _: UnauthorizedAccessException =>  Ok("Access Denied")
      case e: Throwable =>  {
        println(e)
        Ok("Internal Error")
      }
    }
  }
}
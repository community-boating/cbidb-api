package Api.Endpoints.Kiosk

import Api.AuthenticatedRequest
import Api.Endpoints.Public.FlagColor.FlagColorParamsObject
import CbiUtil.ParsedRequest
import IO.PreparedQueries.PreparedQueryForInsert
import IO.PreparedQueries.Public.GetFlagColor
import Services.Authentication.PublicUserType
import Services.CacheBroker
import Services.PermissionsAuthority.UnauthorizedAccessException
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class CreatePerson @Inject() (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  object errors {
    val NOT_JSON = JsObject(Map(
      "code" -> JsString("not_json"),
      "message" -> JsString("Post body not parsable as json.")
    ))
    val BAD_PARAMS = JsObject(Map(
      "code" -> JsString("bad_parameters"),
      "message" -> JsString("JSON parameters were not correct.")
    ))
    val BAD_DATE = JsObject(Map(
      "code" -> JsString("bad_date_format"),
      "message" -> JsString("DOB was not parsable as MM/DD/YYYY")
    ))
    val UNKNOWN = JsObject(Map(
      "code" -> JsString("unknown"),
      "message" -> JsString("An unknwon error occurred.")
    ))
  }
  def post: Action[AnyContent] = Action.async{request =>
    try {
      val rc = getRC(PublicUserType, ParsedRequest(request))
      val cb: CacheBroker = rc.cb
      val pb = rc.pb

      val params = request.body.asJson

      // TODO: if we're keeping this, redo with chained try's or something rather than pyramid of ifs
      if (params.isEmpty) {
        Future { Status(400)(JsObject(Map("error" -> errors.NOT_JSON))) }
      } else {
        try {
          val parsed = CreatePersonParams.apply(params.get)
          val parsedDOB = CbiUtil.DateUtil.parse(parsed.dob)
          println(parsed)
          val q = new PreparedQueryForInsert(Set(PublicUserType)) {
            override def getQuery: String =
              """
                |insert into persons
                |(temp, name_first, name_last, dob, email) values
                | ('N',?,?,to_date(?,'MM/DD/YYYY'),?)
              """.stripMargin
            override val params: List[String] = List(
              parsed.nameFirst,
              parsed.nameLast,
              parsed.dob,
              parsed.email
            )
            override val pkName: Option[String] = Some("person_id")
          }
          val id = pb.executePreparedQueryForInsert(q)

          Future {Ok(JsObject(Map("personID" -> JsNumber(id.getOrElse("-1").toInt))))}
        } catch {
          case e: play.api.libs.json.JsResultException => {
            println(e)
            Future { Status(400)(JsObject(Map("error" -> errors.BAD_PARAMS))) }
          }
          case e: java.time.format.DateTimeParseException => {
            println(e)
            Future { Status(400)(JsObject(Map("error" -> errors.BAD_DATE))) }
          }
          case e: Throwable => {
            println(e)
            Future { Status(400)(JsObject(Map("error" -> errors.UNKNOWN))) }
          }
        }

      }
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }
}

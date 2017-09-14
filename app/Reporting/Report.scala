package Reporting

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.mvc.{Action, Controller, ResponseHeader, Result}


class Report extends Controller {
  def getSampleFile = Action {
    val s: String =
      """
        |dfgdfgdfh
        |dfgh
        |f
        |ghdfghfghdfghd
        |fghdfgh
        |fgh
        |f
        |gh
        |cghcvhfghfghfgh
        |
      """.stripMargin
    val source: Source[ByteString, _] = Source.single(ByteString(s))

    Result(
      header = ResponseHeader(200, Map(
        CONTENT_DISPOSITION -> "attachment; filename=crap.txt"
      )),
      body = HttpEntity.Streamed(source, Some(s.length), Some("application/text"))
    )
  }
}

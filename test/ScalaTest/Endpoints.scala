package ScalaTest

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import play.api.libs.json._

@RunWith(classOf[JUnitRunner])
class Endpoints  extends FunSuite {
  test("Ping") {
    val url = "https://workstation.community-boating.org/api/ping"
    val result = scala.io.Source.fromURL(url).mkString
    assert(result == "pong")
  }

  test("jp-class-instances") {
    val url = "https://workstation.community-boating.org/api/jp-teams"
    val resultString = scala.io.Source.fromURL(url).mkString
    println(resultString)
    val resultJSON = Json.parse(resultString).asInstanceOf[JsObject]
    val data: JsObject = resultJSON("data").asInstanceOf[JsObject]
    val rows: JsArray = data("rows").as[JsArray]
    val firstRow = rows(0).as[JsArray]
    assert(firstRow(0).as[JsString] == JsString("Blue Team"))

  }
}

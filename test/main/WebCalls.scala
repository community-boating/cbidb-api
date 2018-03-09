package main

import IO.HTTP.{FromScalajHTTP, GET}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class WebCalls extends FunSuite {
  test("Ping") {
    val client = new FromScalajHTTP
    val res: Future[Unit] = client.getJSON(
      "http://api.community-boating.org/api/jp-teams",
      GET,
      None,
      None,
      None
    ).map(s => {
      println("done")
      val r = s.asInstanceOf[JsObject]("data")("rows").asInstanceOf[JsArray]

      assert(r.value.length == 3)
    })
    Await.result(res, Duration(10, "seconds"))
  }
}

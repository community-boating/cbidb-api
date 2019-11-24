package org.sailcbi.APIServer.Api.Endpoints

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

class OOME @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post(): Action[AnyContent] = Action { _ => {
		Ok("Uncomment this to create an OOME")
		/*
		var l: List[String] = Nil
		val s: String = "jklcvgjkadrhuisdfjkgsdffuigdfg"
		(1 to 30).foreach(i => {
		  println("Iteration " + i)
		  (1 to Math.pow(5, i).toInt).foreach(j => {
			l = s :: l
			if (j % 5000000 == 0) println("At " + j + "; free: " + Runtime.getRuntime.freeMemory())
		  })
		  println("Free: " + Runtime.getRuntime.freeMemory())
		})
		Ok("boom!")*/
	}
	}
}

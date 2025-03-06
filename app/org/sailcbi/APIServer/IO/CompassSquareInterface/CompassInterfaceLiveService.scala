package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.IO.HTTP.{HTTPMechanism, POST}
import com.coleji.neptune.Util.{Failed, Resolved, ServiceRequestResult}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class CompassInterfaceLiveService(baseURL: String, http: HTTPMechanism) extends CompassInterfaceMechanism {

  override def createCompassOrder(legacyOrderId: Int): Future[ServiceRequestResult[Int, String]] = ???//{
//
//     val a = http.getString(baseURL + s"/create_order/$legacyOrderId", POST, None, None, None).transform({
//       case Success(s: String) => {
//         //  println(jsv.toString())
//         Success(Resolved(s))
//       }
//       case Failure(e: Throwable) => Success(Failed(e))
//     })
//    )
//
//  }

}

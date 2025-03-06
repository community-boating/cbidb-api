package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.Util.ServiceRequestResult

import scala.concurrent.Future

abstract class CompassInterfaceMechanism {
  def createCompassOrder(legacyOrderId: Int): Future[ServiceRequestResult[Int, String]]
}

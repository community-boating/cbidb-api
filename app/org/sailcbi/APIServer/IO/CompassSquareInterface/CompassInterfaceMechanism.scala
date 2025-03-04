package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.Util.ServiceRequestResult

import scala.concurrent.Future

abstract class CompassInterfaceMechanism {
  def getSquareItemIDForClassApSession(classApSessionId: Int) : Future[ServiceRequestResult[Int, String]]
  def getSquareItemIDForMembership()
  def processOrder(items: Array[Int]) : Future[ServiceRequestResult[Int, String]]
}

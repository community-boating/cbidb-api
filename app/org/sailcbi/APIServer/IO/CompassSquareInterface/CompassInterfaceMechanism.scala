package org.sailcbi.APIServer.IO.CompassSquareInterface

import scala.concurrent.Future

abstract class CompassInterfaceMechanism {
  def upsertSquareCustomer(personId: Int): Future[String]
  def upsertCompassOrder(legacyOrderId: Int): Future[String]
  def payCompassOrderViaGiftCard(legacyOrderId: Int, requestBodyJson: String): Future[String]
  def payCompassOrderViaPaymentSource(personId: Option[Int], legacyOrderId: Int, requestBodyJson: String): Future[String]
  def pollCompassOrderStatus(legacyOrderId: Int): Future[String]
  def fetchAPIConstants(): Future[String]
  def storeSquareCard(personId: Int, requestBodyJson: String): Future[String]
  def getSquareGiftCardInfo(personId: Int, requestBodyJson: String): Future[String]
  def clearSquareCard(personId: Int, requestBodyJson: String): Future[String]
}

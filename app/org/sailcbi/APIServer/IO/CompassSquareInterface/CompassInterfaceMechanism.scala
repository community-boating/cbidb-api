package org.sailcbi.APIServer.IO.CompassSquareInterface

import scala.concurrent.Future

abstract class CompassInterfaceMechanism {
  def upsertSquareCustomer(personId: Int): Future[String]
  def upsertCompassOrder(legacyOrderId: Int): Future[String]
  def payCompassOrderViaGiftCard(legacyOrderId: Int, GAN: String): Future[String]
  def payCompassOrderViaPaymentSource(legacyOrderId: Int, paymentSourceId: String): Future[String]
  def pollCompassOrderStatus(legacyOrderId: Int): Future[String]
  def fetchAPIConstants(): Future[String]
}

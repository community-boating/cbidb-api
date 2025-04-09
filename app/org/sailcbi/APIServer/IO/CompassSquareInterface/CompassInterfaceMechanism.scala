package org.sailcbi.APIServer.IO.CompassSquareInterface

import scala.concurrent.Future

abstract class CompassInterfaceMechanism {
  def upsertSquareCustomer(personId: Int): Future[String]
  def upsertCompassOrder(orderAppAlias: String, legacyOrderId: Int): Future[String]
  def payCompassOrderFree(legacyOrderId: Int): Future[String]
  def payCompassOrderViaPaymentSource(personId: Option[Int], legacyOrderId: Int, requestBodyJson: String): Future[String]
  def pollCompassOrderStatus(requestBodyJson: String): Future[String]
  def fetchAPIConstants(): Future[String]
  def storeSquareCard(personId: Int, requestBodyJson: String): Future[String]
  def getSquareGiftCardInfo(personId: Int, requestBodyJson: String): Future[String]
  def clearSquareCard(personId: Int, requestBodyJson: String): Future[String]
  def getStaggeredPaymentInvoices(personId: Int): Future[String]
  def publishStaggeredPaymentInvoice(personId: Int, requestBodyJson: String): Future[String]
}

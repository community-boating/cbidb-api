package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.IO.HTTP.{HTTPMechanism, POST}

import scala.concurrent.{ExecutionContext, Future}

class CompassInterfaceLiveService(baseURL: String, key: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends CompassInterfaceMechanism {

  private def getString(url: String, body: Option[String] = None): Future[String] =
    http.getString(url, POST, body, None, None, Some(key))


  override def upsertSquareCustomer(personId: Int): Future[String] =
    getString(baseURL + "/upsertSquareCustomer/" + personId)

  override def upsertCompassOrder(orderAppAlias: String, legacyOrderId: Int): Future[String] =
    getString(baseURL + "/upsertOrder/" +orderAppAlias + "/" + legacyOrderId)

  override def payCompassOrderFree(legacyOrderId: Int): Future[String] =
    getString(baseURL + "/payOrderFree/" + legacyOrderId)

  override def payCompassOrderViaPaymentSource(personId: Option[Int], legacyOrderId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/payOrderViaPaymentSource/" + legacyOrderId + personId.map(a => "/" + a).getOrElse(""), Some(requestBodyJson))

  override def pollCompassOrderStatus(requestBodyJson: String): Future[String] =
    getString(baseURL + "/pollOrderStatus", Some(requestBodyJson))

  override def fetchAPIConstants(): Future[String] =
    getString(baseURL + "/fetchAPIConstants")

  override def storeSquareCard(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/createCard/" + personId, Some(requestBodyJson))

  override def getSquareGiftCardInfo(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/getGiftCardInfo/" + personId, Some(requestBodyJson))

  override def saveDefaultPaymentMethod(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/saveDefaultPaymentMethod/" + personId, Some(requestBodyJson))

  override def clearSquareCard(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/deleteStoredCard/" + personId, Some(requestBodyJson))

  override def getStaggeredPaymentInvoices(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/getStaggeredPaymentInvoices/" + personId, Some(requestBodyJson))

  override def createStaggeredPaymentInvoice(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/createStaggeredPaymentInvoice/" + personId, Some(requestBodyJson))

  override def payInvoiceNow(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/payInvoiceNow/" + personId, Some(requestBodyJson))

  override def getRecurringDonations(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/getRecurringDonations/" + personId, Some(requestBodyJson))

  override def payRecurringDonations(personId: Int, legacyOrderId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/payRecurringDonations/" + personId + "/" + legacyOrderId, Some(requestBodyJson))

  override def updateRecurringDonation(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/updateRecurringDonation/" + personId, Some(requestBodyJson))

  override def deleteRecurringDonation(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/deleteRecurringDonation/" + personId, Some(requestBodyJson))

}

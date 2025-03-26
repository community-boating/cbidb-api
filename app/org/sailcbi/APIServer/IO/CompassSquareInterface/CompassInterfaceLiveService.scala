package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.IO.HTTP.{HTTPMechanism, POST}

import scala.concurrent.{ExecutionContext, Future}

class CompassInterfaceLiveService(baseURL: String, key: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends CompassInterfaceMechanism {

  private def getString(url: String, body: Option[String] = None): Future[String] =
    http.getString(url, POST, body, None, None, Some(key))


  override def upsertSquareCustomer(personId: Int): Future[String] =
    getString(baseURL + "/upsertSquareCustomer/" + personId)

  override def upsertCompassOrder(legacyOrderId: Int): Future[String] =
    getString(baseURL + "/upsertOrder/" + legacyOrderId)

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

  override def clearSquareCard(personId: Int, requestBodyJson: String): Future[String] =
    getString(baseURL + "/deleteStoredCard/" + personId, Some(requestBodyJson))
}

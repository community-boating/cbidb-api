package org.sailcbi.APIServer.IO.CompassSquareInterface

import com.coleji.neptune.IO.HTTP.{HTTPMechanism, POST}

import scala.concurrent.{ExecutionContext, Future}

class CompassInterfaceLiveService(baseURL: String, key: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends CompassInterfaceMechanism {

  private def getString(url: String): Future[String] =
    http.getString(url, POST, None, None, None, Some(key))


  override def upsertSquareCustomer(personId: Int): Future[String] =
    getString(baseURL + "/upsertSquareCustomer/" + personId)

  override def upsertCompassOrder(legacyOrderId: Int): Future[String] =
    getString(baseURL + "/upsertOrder/" + legacyOrderId)

  override def payCompassOrderViaGiftCard(compassOrderId: Int, GAN: String): Future[String] =
    getString(baseURL + "/payOrderViaGiftCard/" + compassOrderId + "/" + GAN)

  override def payCompassOrderViaPaymentSource(compassOrderId: Int, paymentSourceId: String): Future[String] =
    getString(baseURL + "/payOrderViaPaymentSource/" + compassOrderId + "/" + paymentSourceId)

  override def pollCompassOrderStatus(compassOrderId: Int): Future[String] =
    getString(baseURL + "/pollOrderStatus/" + compassOrderId)

  override def fetchAPIConstants(): Future[String] =
    getString(baseURL + "/fetchAPIConstants")

}

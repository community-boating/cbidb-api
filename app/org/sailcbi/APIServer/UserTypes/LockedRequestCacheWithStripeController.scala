package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{DatabaseGateway, LockedRequestCache}
import com.coleji.framework.IO.HTTP.FromWSClient
import com.coleji.framework.Util.PropertiesWrapper
import org.sailcbi.APIServer.IO.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.IO.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Server.CBIBootLoaderLive
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

abstract class LockedRequestCacheWithStripeController(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway)
extends LockedRequestCache(userName, serverParams, dbGateway) {
	private def getStripeAPIIOMechanism(ws: WSClient)(implicit exec: ExecutionContext): StripeAPIIOMechanism = new StripeAPIIOLiveService(
		"https://api.stripe.com/v1/",
		serverParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.STRIPE_API_KEY),
		new FromWSClient(ws)
	)

	private lazy val stripeDatabaseIOMechanism = new StripeDatabaseIOMechanism(pb)

	def getStripeIOController(ws: WSClient)(implicit exec: ExecutionContext): StripeIOController = new StripeIOController(
		this,
		getStripeAPIIOMechanism(ws),
		stripeDatabaseIOMechanism,
		PA.logger
	)
}

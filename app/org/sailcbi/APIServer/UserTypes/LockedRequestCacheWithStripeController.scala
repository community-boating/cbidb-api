package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{LockedRequestCache, PermissionsAuthority}
import com.coleji.framework.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.IO.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

abstract class LockedRequestCacheWithStripeController(override val userName: String, secrets: PermissionsAuthoritySecrets)
extends LockedRequestCache(userName, secrets) {
	private def getStripeAPIIOMechanism(ws: WSClient)(implicit exec: ExecutionContext): StripeAPIIOMechanism = new StripeAPIIOLiveService(
		PermissionsAuthority.stripeURL,
		secrets.stripeSecretKey,
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

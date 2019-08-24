package org.sailcbi.APIServer.Services.Boot

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import org.sailcbi.APIServer.Services._
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Mode}

import scala.concurrent.Future

class ServerBootLoaderLive @Inject()(lifecycle: ApplicationLifecycle) extends ServerBootLoader {
	val PA = this.load(Some(lifecycle), false)
	println("Live loader::::: setting PA!")
	PermissionsAuthority.setPA(PA)
}
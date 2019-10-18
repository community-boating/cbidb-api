package org.sailcbi.APIServer.Services.Boot

import javax.inject.Inject
import org.sailcbi.APIServer.Services._
import play.api.inject.ApplicationLifecycle

class ServerBootLoaderLive @Inject()(lifecycle: ApplicationLifecycle) extends ServerBootLoader {
	val PA = this.load(Some(lifecycle), false, false)
	println("Live loader::::: setting PA!")
	PermissionsAuthority.setPA(PA)
	PA.procedureTest()
}
package com.coleji.framework.Core.Boot

import com.coleji.framework.Core.PermissionsAuthority
import org.sailcbi.APIServer.Services._
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class ServerBootLoaderLive @Inject()(lifecycle: ApplicationLifecycle) extends ServerBootLoader {
	val PA = this.load(Some(lifecycle), false, false)
	PA.instantiateAllEntityCompanions()
	println("Live loader::::: setting PA!")
	PermissionsAuthority.setPA(PA)
	PA.bootChecks()
	//PA.procedureTest()
}

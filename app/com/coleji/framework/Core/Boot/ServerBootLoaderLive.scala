package com.coleji.framework.Core.Boot

import com.coleji.framework.Core.{PermissionsAuthority, RequestCacheObject}
import com.coleji.framework.Util.PropertiesWrapper
import play.api.inject.ApplicationLifecycle

class ServerBootLoaderLive {
	protected def init(
		lifecycle: ApplicationLifecycle,
		entityPackagePath: String,
		definedAuthMechanisms: List[(RequestCacheObject[_], String, PropertiesWrapper => Boolean)],
		requiredProperties: List[String],
		paPostBoot: PropertiesWrapper => Unit
	): PermissionsAuthority = {
		val pa = ServerBootLoader.load(Some(lifecycle), isTestMode = false, readOnlyDatabase = false, entityPackagePath, definedAuthMechanisms, requiredProperties, paPostBoot)
		println("Live loader::::: setting PA!")
		PermissionsAuthority.setPA(pa)
		println("Starting entity boot")
		pa.instantiateAllEntityCompanions(entityPackagePath)
		println("finished entity boot")
		pa.bootChecks()
		pa
	}
}

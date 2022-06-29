package com.coleji.neptune.Core.Boot

import com.coleji.neptune.Core.{PermissionsAuthority, RequestCacheObject}
import com.coleji.neptune.Util.PropertiesWrapper
import play.api.inject.ApplicationLifecycle

import java.nio.charset.Charset

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
		println("Default charset: " + Charset.defaultCharset().displayName())
		PermissionsAuthority.setPA(pa)
		println("Starting entity boot")
		pa.instantiateAllEntityCompanions(entityPackagePath)
		println("finished entity boot")
		pa.bootChecks()
		pa
	}
}

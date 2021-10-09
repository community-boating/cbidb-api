package com.coleji.neptune.Core.Boot

import com.coleji.neptune.Core.{PermissionsAuthority, RequestCache, RequestCacheObject}
import com.coleji.neptune.Storable.StorableClass
import org.sailcbi.APIServer.Server.CBIBootLoaderLive

class ServerBootLoaderTest(writeableDatabases: Set[String]) {
	def init(entityPackagePath: String, writeable: Boolean): PermissionsAuthority = ServerBootLoader.load(
		lifecycle = None,
		isTestMode = true,
		readOnlyDatabase= !writeable,
		entityPackagePath = entityPackagePath,
		definedAuthMechanisms = List.empty,
		requiredProperties = List.empty,
		paPostBoot = _ => {}
	)

	def withPA(block: PermissionsAuthority => Unit): Unit = {
		val pa = this.init(CBIBootLoaderLive.ENTITY_PACKAGE_PATH, false)
		PermissionsAuthority.setPA(pa)
		pa.bootChecks()
		block(pa)
		pa.closeDB()
		PermissionsAuthority.clearPA()
	}

	def withPAWriteable(block: PermissionsAuthority => Unit): Unit = {
		val pa = this.init(CBIBootLoaderLive.ENTITY_PACKAGE_PATH, true)
		if (!writeableDatabases.contains(pa.instanceName)) {
			pa.closeDB()
			throw new Exception("Cowardly refusing to provide a writeable PB on an unregistered database: " + pa.instanceName)
		} else {
			PermissionsAuthority.setPA(pa)
			pa.bootChecks()
			block(pa)
			pa.closeDB()
			PermissionsAuthority.clearPA()
		}
	}

	def assertRC[T <: RequestCache](pa: PermissionsAuthority)(rco: RequestCacheObject[T], userName: String): T = pa.assertRC(rco, userName)

	def nukeDB(pa: PermissionsAuthority): Unit = pa.nukeDB()

	def closeDB(pa: PermissionsAuthority): Unit = pa.closeDB()

	def withSeedState(pa: PermissionsAuthority)(entities: List[StorableClass], block: () => Unit): Unit = pa.withSeedState(entities, block)
}
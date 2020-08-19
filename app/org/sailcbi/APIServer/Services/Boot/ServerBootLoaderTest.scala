package org.sailcbi.APIServer.Services.Boot

import org.sailcbi.APIServer.Services.PermissionsAuthority

class ServerBootLoaderTest extends ServerBootLoader {
	def pa(writeable: Boolean): PermissionsAuthority = this.load(None, true, !writeable)
}

object ServerBootLoaderTest {
	def withPA(block: PermissionsAuthority => Unit): Unit = {
		val loader = new ServerBootLoaderTest()
		val pa = loader.pa(false)
		PermissionsAuthority.setPA(pa)
		pa.bootChecks()
		block(pa)
		pa.closeDB()
		PermissionsAuthority.clearPA()
	}
	def withPAWriteable(block: PermissionsAuthority => Unit): Unit = {
		val acceptableWriteableDatabases = Set(
			"CBI_DEV2"
		)
		val loader = new ServerBootLoaderTest()
		val pa = loader.pa(true)
		if (!acceptableWriteableDatabases.contains(pa.instanceName)) {
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
}
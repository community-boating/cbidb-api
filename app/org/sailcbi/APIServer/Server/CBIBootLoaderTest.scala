package org.sailcbi.APIServer.Server

import com.coleji.neptune.Core.Boot.ServerBootLoaderTest

class CBIBootLoaderTest extends ServerBootLoaderTest(CBIBootLoaderTest.writeableDatabases, CBIBootLoaderLive.ENTITY_PACKAGE_PATH) {

}

object CBIBootLoaderTest {
	val writeableDatabases = Set(
		"CBI_DEV2"
	)
}
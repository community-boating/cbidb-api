package org.sailcbi.APIServer.Server

import com.coleji.framework.Core.Boot.ServerBootLoaderTest

class CBIBootLoaderTest extends ServerBootLoaderTest(CBIBootLoaderTest.writeableDatabases) {

}

object CBIBootLoaderTest {
	val writeableDatabases = Set(
		"CBI_DEV2"
	)
}
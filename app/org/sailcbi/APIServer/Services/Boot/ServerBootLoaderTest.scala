package org.sailcbi.APIServer.Services.Boot

import org.sailcbi.APIServer.Services.{PermissionsAuthority}

class ServerBootLoaderTest extends ServerBootLoader {
	PermissionsAuthority.setPA(this.load(None, true))
}

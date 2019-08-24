package org.sailcbi.APIServer.Services.Boot

import org.sailcbi.APIServer.Services.{OracleConnectionPoolConstructor, PermissionsAuthority}

class ServerBootLoaderTest extends ServerBootLoader {
	PermissionsAuthority.setPA(this.load(new OracleConnectionPoolConstructor(), None, play.api.Mode.Test))
}

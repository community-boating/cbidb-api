package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.Services.Authentication.UserType

class OracleBroker private[Services](dbConnection: DatabaseHighLevelConnection, rc: RequestCache[_], preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, rc, preparedQueriesOnly, readOnly)

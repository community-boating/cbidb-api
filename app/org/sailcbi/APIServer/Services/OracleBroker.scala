package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.Services.Authentication.UserType

class OracleBroker[T <: UserType] private[Services](dbConnection: DatabaseHighLevelConnection, rc: RequestCache[T], preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker[T](dbConnection, rc, preparedQueriesOnly, readOnly)
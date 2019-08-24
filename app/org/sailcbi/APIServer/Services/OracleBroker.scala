package org.sailcbi.APIServer.Services

class OracleBroker private[Services](dbConnection: DatabaseConnection, rc: RequestCache, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, rc, preparedQueriesOnly, readOnly)
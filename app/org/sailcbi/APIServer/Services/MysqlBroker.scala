package org.sailcbi.APIServer.Services

class MysqlBroker private[Services](dbConnection: DatabaseConnection, rc: RequestCache, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, rc, preparedQueriesOnly, readOnly)
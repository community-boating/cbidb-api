package org.sailcbi.APIServer.Services

class MysqlBroker private[Services](dbConnection: DatabaseHighLevelConnection, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, preparedQueriesOnly, readOnly)

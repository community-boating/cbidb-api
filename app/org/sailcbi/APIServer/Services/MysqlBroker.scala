package org.sailcbi.APIServer.Services

class MysqlBroker private[Services](dbConnection: DatabaseHighLevelConnection, rc: RequestCache[_], preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, rc, preparedQueriesOnly, readOnly)

package org.sailcbi.APIServer.Services

class MysqlBroker private[Services](rc: RequestCache, preparedQueriesOnly: Boolean) extends RelationalBroker(rc, preparedQueriesOnly)
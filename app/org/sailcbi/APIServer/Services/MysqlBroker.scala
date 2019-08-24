package org.sailcbi.APIServer.Services

class MysqlBroker private[Services](rc: RequestCache, preparedQueriesOnly: Boolean, readOnly: Boolean) extends RelationalBroker(rc, preparedQueriesOnly, readOnly)
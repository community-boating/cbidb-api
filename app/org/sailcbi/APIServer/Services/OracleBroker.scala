package org.sailcbi.APIServer.Services

class OracleBroker private[Services](rc: RequestCache, preparedQueriesOnly: Boolean) extends RelationalBroker(rc, preparedQueriesOnly)
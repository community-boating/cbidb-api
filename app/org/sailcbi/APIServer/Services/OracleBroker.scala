package org.sailcbi.APIServer.Services

class OracleBroker private[Services](rc: RequestCache, preparedQueriesOnly: Boolean, readOnly: Boolean) extends RelationalBroker(rc, preparedQueriesOnly, readOnly)
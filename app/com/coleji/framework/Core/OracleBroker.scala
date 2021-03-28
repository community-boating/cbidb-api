package com.coleji.framework.Core

class OracleBroker private[Core](dbConnection: DatabaseHighLevelConnection, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, preparedQueriesOnly, readOnly)

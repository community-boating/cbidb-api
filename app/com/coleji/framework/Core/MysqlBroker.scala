package com.coleji.framework.Core

class MysqlBroker private[Core](dbConnection: DatabaseHighLevelConnection, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, preparedQueriesOnly, readOnly)

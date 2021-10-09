package com.coleji.neptune.Core

class MysqlBroker private[Core](dbConnection: DatabaseGateway, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbConnection, preparedQueriesOnly, readOnly)

package com.coleji.framework.Core

// Unfortunately the name DatabaseConnection already means a specific thing
// DBHLC represents the application's overall connection to the database, basically the connection pools
// plus any other information a single connection might need, e.g. schema name, query flags etc
case class DatabaseHighLevelConnection(
										  mainPool: ConnectionPoolWrapper,
										  tempPool: ConnectionPoolWrapper,
										  mainSchemaName: String,
										  tempSchemaName: String,
										  mainUserName: String
									  ) {
	def close(): Unit = {
		System.out.println("  ************    Shutting down!  Closing pool!!  *************  ")
		mainPool.close()
		tempPool.close()
	}
}

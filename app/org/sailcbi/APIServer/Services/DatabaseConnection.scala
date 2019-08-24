package org.sailcbi.APIServer.Services

case class DatabaseConnection (
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
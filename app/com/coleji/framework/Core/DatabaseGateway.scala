package com.coleji.framework.Core

class DatabaseGateway(
	private[Core] val mainPool: ConnectionPoolWrapper,
	private[Core] val tempPool: ConnectionPoolWrapper,
	private[Core] val mainSchemaName: String,
	private[Core] val tempSchemaName: String,
	private[Core] val mainUserName: String
) {
	private[Core] def close(): Unit = {
		System.out.println("  ************    Shutting down!  Closing pool!!  *************  ")
		mainPool.close()
		tempPool.close()
	}
}

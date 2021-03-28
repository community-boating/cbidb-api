package org.sailcbi.APIServer.Server

import com.coleji.framework.Core.DatabaseHighLevelConnection

case class PermissionsAuthoritySecrets(
										  dbConnection: DatabaseHighLevelConnection,
										  apexToken: String,
										  kioskToken: String,
										  apexDebugSignet: Option[String],
										  symonSalt: Option[String],
										  stripeSecretKey: String,
										  sentryDSN: Option[String]
									  )
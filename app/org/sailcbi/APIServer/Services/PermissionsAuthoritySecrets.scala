package org.sailcbi.APIServer.Services

case class PermissionsAuthoritySecrets(
											  dbConnection: DatabaseHighLevelConnection,
											  apexToken: String,
											  kioskToken: String,
											  apexDebugSignet: Option[String],
											  symonSalt: Option[String],
											  stripeSecretKey: String,
											  sentryDSN: Option[String]
)

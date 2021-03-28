package org.sailcbi.APIServer.Server

import com.coleji.framework.Core.{RequestCacheObject, RootRequestCache}
import com.coleji.framework.Util.PropertiesWrapper
import org.sailcbi.APIServer.UserTypes._

class ServerInstanceProperties(fileLocation: String) extends PropertiesWrapper(fileLocation, ServerInstanceProperties.requiredProperties) {
	// 3rd member is a function that returns true if the user type is permitted, false if we need to force-disable it even if conf says enable
	// TODO: verify this is the order we want.  If someone can auth as multiple of these, first one wins.  Member has to go before protoperson e.g.
	private val definedAuthMechanisms: List[(RequestCacheObject[_], String, () => Boolean)] = List(
		(RootRequestCache, "RootAuthEnabled", () => true),
		(BouncerRequestCache, "BouncerAuthEnabled", () => true),
		(ApexRequestCache, "ApexAuthEnabled", () => true),
		(KioskRequestCache, "KioskAuthEnabled", () => true),
		(SymonRequestCache, "SymonAuthEnabled", () => this.getOptionalString("SymonSalt").isDefined),
		(StaffRequestCache, "StaffAuthEnabled", () => true),
		(MemberRequestCache, "MemberAuthEnabled", () => true),
		(ProtoPersonRequestCache, "ProtoPersonAuthEnabled", () => true)
	)

	val enabledAuthMechanisms: List[RequestCacheObject[_]] =
		definedAuthMechanisms
				.filter(t => this.getBoolean(t._2))
				.filter(t => t._3()) // check the nuke function
				.map(t => t._1)
}

object ServerInstanceProperties {
	val requiredProperties = List(
		"MemberAuthEnabled",
		"KioskAuthEnabled",
		"StaffAuthEnabled",
		"ApexAuthEnabled",
		"SymonAuthEnabled",
		"BouncerAuthEnabled",
		"RootAuthEnabled",
		"ProtoPersonAuthEnabled",
		"ApexToken",
		"ApexDebugSignet",
		"StripeAPIKey",
		"PreparedQueriesOnly",
		"RoutesSecurityLevel",
		"SymonSalt",
		"KioskToken",
		"sentryDSN"
	)
}

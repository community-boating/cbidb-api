package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.PropertiesWrapper
import org.sailcbi.APIServer.Services.Authentication._

class ServerInstanceProperties(fileLocation: String) extends PropertiesWrapper(fileLocation, ServerInstanceProperties.requiredProperties) {
	// 3rd member is a function that returns true if the user type is permitted, false if we need to force-disable it even if conf says enable
	// TODO: verify this is the order we want.  If someone can auth as multiple of these, first one wins.  Member has to go before protoperson e.g.
	private val definedAuthMechanisms: List[(UserTypeObject[_], String, () => Boolean)] = List(
		(RootUserType, "RootAuthEnabled", () => true),
		(BouncerUserType, "BouncerAuthEnabled", () => true),
		(ApexUserType, "ApexAuthEnabled", () => true),
		(KioskUserType, "KioskAuthEnabled", () => true),
		(SymonUserType, "SymonAuthEnabled", () => getPropAsOption("SymonSalt").isDefined),
		(StaffUserType, "StaffAuthEnabled", () => true),
		(MemberUserType, "MemberAuthEnabled", () => true),
		(ProtoPersonUserType, "ProtoPersonAuthEnabled", () => true)
	)

	val enabledAuthMechanisms: List[UserTypeObject[_]] =
		definedAuthMechanisms
				.filter(t => getRequiredBoolean(t._2))
				.filter(t => t._3()) // check the nuke function
				.map(t => t._1)

	private def getRequiredBoolean(p: String): Boolean = getPropAsOption(p) match {
		case Some("true") => true
		case Some("false") => false
		case _ => throw new Exception("Required server property " + p + " was not set or not valid.")
	}

	private def getPropAsOption(p: String): Option[String] = {
		try {
			val prop = this.getProperty(p)
			if (prop == null) None
			else Some(prop)
		} catch {
			case _ => None
		}
	}
}

object ServerInstanceProperties {
	val requiredProperties: Array[String] = Array(
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

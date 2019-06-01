package Services

import CbiUtil.PropertiesWrapper
import Services.Authentication._

class ServerInstanceProperties(fileLocation: String) extends PropertiesWrapper(fileLocation) {
	// 3rd member is a function that returns true if the user type is permitted, false if we need to force-disable it even if conf says enable
	private val definedAuthMechanisms: Set[(UserType, String, () => Boolean)] = Set(
		(MemberUserType, "MemberAuthEnabled", () => true),
		(StaffUserType, "StaffAuthEnabled", () => true),
		(ApexUserType, "ApexAuthEnabled", () => true),
		(KioskUserType, "KioskAuthEnabled", () => true),
		(SymonUserType, "SymonAuthEnabled", () => getPropAsOption("SymonSalt").isDefined)
	)

	val enabledAuthMechanisms: Set[UserType] =
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

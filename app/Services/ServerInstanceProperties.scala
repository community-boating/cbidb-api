package Services

import CbiUtil.PropertiesWrapper
import Services.Authentication._

class ServerInstanceProperties(fileLocation: String) extends PropertiesWrapper(fileLocation) {
  private val definedAuthMechanisms: Set[(UserType, String)] = Set(
    (MemberUserType, "MemberAuthEnabled"),
    (StaffUserType, "StaffAuthEnabled"),
    (ApexUserType, "ApexAuthEnabled"),
    (SymonUserType, "SymonAuthEnabled"),
    (BouncerUserType, "BouncerAuthEnabled"),
    (RootUserType, "RootAuthEnabled")
  )

  val enabledAuthMechanisms:Set[UserType] =
    definedAuthMechanisms
    .filter(t => getRequiredBoolean(t._2))
    .map(t => t._1)

  private def getRequiredBoolean(p: String): Boolean = getPropAsOption(p) match {
    case Some("true") => true
    case Some("false") => false
    case _ => throw new Exception("Required server property " + p + " was not set or not valid.")
  }

  private def getPropAsOption(p: String): Option[String] = {
    try{
      Some(this.getProperty(p))
    } catch {
      case _ => None
    }
  }
}

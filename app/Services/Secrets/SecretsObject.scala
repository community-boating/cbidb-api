package Services.Secrets

import Services.Authentication.ApexUserType

class SecretsObject private[Services] {
  val stripeAPIKey = new Secret(rc => rc.auth.userType == ApexUserType)
}

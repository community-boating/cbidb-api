package Services

import Services.Authentication.UserType

class OracleBroker private[Services] (ut: UserType) extends RelationalBroker(ut)
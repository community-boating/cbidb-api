package Services

import Services.Authentication.UserType

class MysqlBroker private[Services] (ut: UserType) extends RelationalBroker(ut)
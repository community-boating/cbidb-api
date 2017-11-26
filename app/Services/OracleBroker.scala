package Services

class OracleBroker private[Services] extends RelationalBroker {
  val MAX_EXPR_IN_LIST: Int = 900
}
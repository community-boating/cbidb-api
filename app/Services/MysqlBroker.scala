package Services

class MysqlBroker private[Services] extends RelationalBroker {
  val MAX_EXPR_IN_LIST: Int = 900
}
package Services

trait PersistenceBrokerStatic

trait RelationalBrokerStatic extends PersistenceBrokerStatic {
	val MAX_EXPR_IN_LIST: Int
}

object OracleBrokerStatic extends RelationalBrokerStatic {
	val MAX_EXPR_IN_LIST: Int = 900
}

object MysqlBrokerStatic extends RelationalBrokerStatic {
	val MAX_EXPR_IN_LIST: Int = 900
}
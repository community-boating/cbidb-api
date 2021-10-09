package com.coleji.neptune.Core

trait PersistenceBrokerStatic

trait RelationalBrokerStatic extends PersistenceBrokerStatic {
	val MAX_EXPR_IN_LIST: Int
}

object OracleBrokerStatic extends RelationalBrokerStatic {
	override val MAX_EXPR_IN_LIST: Int = 900
}

object MysqlBrokerStatic extends RelationalBrokerStatic {
	override val MAX_EXPR_IN_LIST: Int = 900
}
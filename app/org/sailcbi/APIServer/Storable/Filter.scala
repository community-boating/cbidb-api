package org.sailcbi.APIServer.Storable

// table alias => whole sql string
case class Filter(safeSql: String) {
	override def toString: String = safeSql
}

object Filter {
	def and(fs: List[Filter]): Filter = ???
	def or(fs: List[Filter]): Filter = ???
}
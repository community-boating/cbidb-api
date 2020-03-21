package org.sailcbi.APIServer.Storable

case class Filter(safeSql: String) {
	override def toString: String = safeSql
}

object Filter {
	def and(fs: List[Filter]): Filter = Filter("( " + fs.map(_.safeSql).mkString(" AND ") + ") ")
	def or(fs: List[Filter]): Filter = Filter("( " + fs.map(_.safeSql).mkString(" OR ") + ") ")
}
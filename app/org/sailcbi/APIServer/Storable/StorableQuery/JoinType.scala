package org.sailcbi.APIServer.Storable.StorableQuery

sealed abstract class JoinType  {
	val joinText: String
}

object INNER_JOIN extends JoinType {
	override val joinText: String = "INNER JOIN"
}

object LEFT_OUTER_JOIN extends JoinType {
	override val joinText: String = "LEFT OUTER JOIN"
}
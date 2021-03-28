package com.coleji.framework.IO.PreparedQueries

import org.sailcbi.APIServer.UserTypes.RootRequestCache
import org.sailcbi.APIServer.Services.RequestCacheObject

import java.sql.CallableStatement

abstract class PreparedProcedureCall[T](
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	/**
	 * Map of param name to datatype (e.g. java.sql.Types.INTEGER)
	 * @return
	 */
	def registerOutParameters: Map[String, Int]

	/**
	 * Map of param name to int value
	 * @return
	 */
	def setInParametersInt: Map[String, Int] = Map.empty

	/**
	 * Map of param name to varchar value
	 * @return
	 */
	def setInParametersVarchar: Map[String, String] = Map.empty

	def setInParametersDouble: Map[String, Double] = Map.empty

	// Date params DO NOT WORK
	// Everything will appear to work and then it will act as though the transaction was not committed
	// Send strings instead and cast to date oracle-side
	// def setInParametersDateTime: Map[String, LocalDateTime] = Map.empty

	def getOutResults(cs: CallableStatement): T
}

object PreparedProcedureCall {
	val test = new PreparedProcedureCall[(Int, Int, String, Int)](Set(RootRequestCache)) {
		val i_a = "i_a"
		val o_b = "o_b"
		val io_c = "io_c"
		val i_s = "i_s"
		val o_ss = "o_ss"
		val o_persons = "o_persons"

		override def registerOutParameters: Map[String, Int] = Map(
			o_b -> java.sql.Types.INTEGER,
			io_c -> java.sql.Types.INTEGER,
			o_ss -> java.sql.Types.VARCHAR,
			o_persons -> java.sql.Types.INTEGER,
		)

		override def setInParametersInt: Map[String, Int] = Map(
			i_a -> 55,
			io_c -> 492
		)

		override def setInParametersVarchar: Map[String, String] = Map(
			i_s -> "TestString"
		)

		override def setInParametersDouble: Map[String, Double] = Map.empty
		override def getOutResults(cs: CallableStatement): (Int, Int, String, Int) = (
			cs.getInt(o_b),
			cs.getInt(io_c),
			cs.getString(o_ss),
			cs.getInt(o_persons)
		)

		override def getQuery: String = "JDBC_TEST(?, ?, ?, ?, ?, ?)"
	}
}
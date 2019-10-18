package org.sailcbi.APIServer.IO.PreparedQueries

import java.sql.CallableStatement

import org.sailcbi.APIServer.Services.Authentication.{RootUserType, UserType}

abstract class PreparedProcedureCall[T](
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	/**
	 * Map of 1-indexed param position, to datatype (e.g. java.sql.Types.INTEGER)
	 * @return
	 */
	def registerOutParameters: Map[String, Int]

	/**
	 * Map of 1-indexed param position, to in value
	 * @return
	 */
	def setInParametersInt: Map[String, Int]

	/**
	 * Map of 1-indexed param position, to in value
	 * @return
	 */
	def setIntParametersVarchar: Map[String, String]

	def getOutResults(cs: CallableStatement): T
}

object PreparedProcedureCall {
	val test = new PreparedProcedureCall[(Int, Int, String, Int)](Set(RootUserType)) {
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

		override def setIntParametersVarchar: Map[String, String] = Map(
			i_s -> "TestString"
		)
		override def getOutResults(cs: CallableStatement): (Int, Int, String, Int) = (
			cs.getInt(o_b),
			cs.getInt(io_c),
			cs.getString(o_ss),
			cs.getInt(o_persons)
		)

		override def getQuery: String = "JDBC_TEST(?, ?, ?, ?, ?, ?)"
	}
}
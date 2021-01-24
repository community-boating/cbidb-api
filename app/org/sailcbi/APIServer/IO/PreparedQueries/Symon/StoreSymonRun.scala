package org.sailcbi.APIServer.IO.PreparedQueries.Symon

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForInsert
import org.sailcbi.APIServer.Services.Authentication.SymonRequestCache

class StoreSymonRun(
						   host: String,
						   program: String,
						   argString: String,
						   status: Int,
						   mac: String,
						   symonVersion: Option[String]
				   ) extends PreparedQueryForInsert(Set(SymonRequestCache), true) {

	val getQuery: String =
		s"""
		   |insert into symon_runs(HOST_NAME, PROGRAM_NAME, STATUS, MAC_ADDRESS, RUN_DATETIME, ARG_STRING, symon_version)
		   | values (?, ?, ?, ?, sysdate, ?, ?)
		   |
    """.stripMargin

	override val params: List[String] = List(
		host, program, status.toString, mac, argString, symonVersion.getOrElse(null)
	)
	override val pkName: Option[String] = Some("RUN_ID")
}

package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class ApRosterData(
						  val personId: Int,
						  val firstName: String,
						  val lastName: String,
						  val isWaitList: Boolean,
						  val allergies: Option[String],
						  val medications: Option[String],
						  val specialNeeds: Option[String],
						  val signupNote: Option[String],
						  val numberSessions: Initializable[Int]
				  ) extends RowData {
	lazy val cellValues = {
		List(
			lastName + ", " + firstName
		) ::: (1 to numberSessions.getOrElse(1) + 1).map(_ => " ").toList
	}
}
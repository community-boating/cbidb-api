package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Initializable

class JpRosterData(
	val personId: Int,
	val firstName: String,
	val lastName: String,
	val isWaitList: Boolean,
	val allergies: Option[String],
	val medications: Option[String],
	val specialNeeds: Option[String],
	val signupNote: Option[String],
	val numberSessions: Initializable[Int],
	val sectionName: Option[String]
) extends RowData {
	lazy val cellValues = {
		List(
			lastName + ", " + firstName
		) ::: (1 to numberSessions.getOrElse(1) + 1).map(_ => " ").toList
	}
}
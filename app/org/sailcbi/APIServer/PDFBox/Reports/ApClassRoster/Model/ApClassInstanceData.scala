package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model

import java.time.ZonedDateTime

case class ApClassInstanceData(
	instanceId: Int,
	typeName: String,
	numberSessions: Int,
	firstSessionTime: ZonedDateTime
)
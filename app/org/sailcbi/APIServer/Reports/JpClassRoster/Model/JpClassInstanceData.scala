package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import java.time.ZonedDateTime

case class JpClassInstanceData(
	instanceId: Int,
	typeName: String,
	numberSessions: Int,
	firstSessionTime: ZonedDateTime
)
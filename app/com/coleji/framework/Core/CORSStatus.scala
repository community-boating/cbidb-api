package com.coleji.framework.Core

sealed abstract class CORSStatus(val status: String)

case object SAME_SITE extends CORSStatus("SameSite")

case object CROSS_SITE extends CORSStatus("CrossSite")

case object UNKNOWN extends CORSStatus("Unknown")

object CORSStatus {
	val statuses = List(
		SAME_SITE,
		CROSS_SITE,
		UNKNOWN
	)
}
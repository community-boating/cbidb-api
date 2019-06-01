package Services

import play.api.mvc.Headers

object CORS {
	def getCORSStatus(headers: Headers): Option[CORSStatus] = {
		val corsStatusHeaderName = "CBI-CORS-Status"
		val matches = CORSStatus.statuses.filter(_.status == headers(corsStatusHeaderName))
		if (matches.length == 1) Some(matches(0))
		else None
	}
}

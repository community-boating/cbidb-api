package Services.Emailer

abstract class Emailer {
	def send(subject: String, body: String, to: String = "jon@community-boating.org"): Unit
}

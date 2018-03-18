package Services.Emailer

abstract class Emailer {
  def send(to: String, subject: String, body: String): Unit
}

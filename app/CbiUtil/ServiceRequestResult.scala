package CbiUtil

sealed abstract class ServiceRequestResult

// Everything working fine; nothing to say, nothing to do.  Maybe return some result
case class Succeeded[T](t: T) extends ServiceRequestResult

// Something went wrong that's preventing the request from finishing.  The end user needs to change their request; no alarm needs to be sounded.
// E.g. a credit card was declined
case class ValidationError(customerMessage: Option[String]) extends ServiceRequestResult

// Something went wrong that we should sound an internal alarm about, but we can still complete the user's request normally.
// E.g. a cc charge was successful but the charge record failed to write to the database
class InternalWarning(internalAction: => Option[Unit]) extends ServiceRequestResult

// Something went wrong that is preventing the request from completing, and is an alarm
// e.g. the remote credit card processing service is not responding to our requests
class CriticalError(customerMessage: Option[String], internalAction: => Option[Unit]) extends ServiceRequestResult
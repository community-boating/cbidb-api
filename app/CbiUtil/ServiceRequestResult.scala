package CbiUtil

sealed abstract class ServiceRequestResult

// May or may not have generated an internal warning but either way we're gonna keep goin
sealed abstract class NetSuccess extends ServiceRequestResult

// Might be routine, might be a thermonuclear emergency, either way this request is done son
sealed abstract class NetFailure extends ServiceRequestResult

// Everything working fine; nothing to say, nothing to do.  Maybe return some result
case class Succeeded[T](t: T) extends NetSuccess

// Something went wrong that we should sound an internal alarm about, but we can still complete the user's request normally.
// E.g. a cc charge was successful but the charge record failed to write to the database
class Warning(internalAction: => Option[Unit]) extends NetSuccess

// Something went wrong that's preventing the request from finishing.  The end user needs to change their request; no alarm needs to be sounded.
// E.g. a credit card was declined
case class ValidationError(customerMessage: Option[String]) extends NetFailure

// Something went wrong that is preventing the request from completing, and is an alarm
// e.g. a 3rd party service is not responding to our requests, or the database disappeared or something
class CriticalError(customerMessage: Option[String], internalAction: => Option[Unit]) extends NetFailure
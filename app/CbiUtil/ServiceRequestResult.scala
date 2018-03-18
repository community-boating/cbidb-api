package CbiUtil

sealed abstract class ServiceRequestResult[T, U]

// May or may not have generated an internal warning but either way we're gonna keep goin
sealed abstract class NetSuccess[T, U](val successObject: T) extends ServiceRequestResult[T, U]

// Might be routine, might be a thermonuclear emergency, either way this request is done son
sealed abstract class NetFailure[T, U] extends ServiceRequestResult[T, U]

// Everything working fine; nothing to say, nothing to do.  Maybe return some result
case class Succeeded[T, U](override val successObject: T) extends NetSuccess[T, U](successObject)

// Something went wrong that we should sound an internal alarm about, but we can still complete the user's request normally.
// E.g. a cc charge was successful but the charge record failed to write to the database
case class Warning[T, U](override val successObject: T) extends NetSuccess[T, U](successObject)

// Something went wrong that's preventing the request from finishing.  The end user needs to change their request; no alarm needs to be sounded.
// E.g. a credit card was declined
case class ValidationError[T, U](errorObject: U) extends NetFailure[T, U]

// Something went wrong that is preventing the request from completing, and is an alarm
// e.g. a 3rd party service is not responding to our requests, or the database disappeared or something
case class CriticalError[T, U](e: Throwable) extends NetFailure[T, U]
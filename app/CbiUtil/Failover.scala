package CbiUtil

// Cheap imitation of javascript promise architecture for chaining promises in a try-catch use case (vs async use case)
// Key difference is once a thing fails, its a `Rejected` forever.  `andCatch` does not ever redeem a failed thing
// (i.e. any `andThen`'s after an exception will never run
// Seems like the only way to do it in a static typing environment
sealed abstract class Failover[A, B] {
	def isResolved: Boolean

	def isFailed: Boolean

	def isRejected: Boolean

	// If called on a Resolved, pipe that success value into the new block and return a Resolved out of that
	// If called on a Rejection or an uncaught Failure, pass it on through
	def andThen[C](block: (A => C)): Failover[C, B] = this match {
		case Resolved(a) => {
			try {
				Resolved(block(a))
			} catch {
				case e: Throwable => Failed(e)
			}
		}
		case Failed(e) => Failed(e)
		case Rejected(b) => Rejected(b)
	}

	// If called on a Resolved, pipe that success value into the new block and return a Resolved out of that
	// If called on a Rejection or an uncaught Failure, pass it on through
	def andThenWithCatch[C](block: (A => C), onFailure: (Throwable => B)): Failover[C, B] = this match {
		case Resolved(a) => {
			try {
				Resolved(block(a))
			} catch {
				case e: Throwable => Rejected(onFailure(e))
			}
		}
		case Failed(e) => Failed(e)
		case Rejected(b) => Rejected(b)
	}

	// If called on a Resolved, pipe that success value into the new block and return a new Resolved out of that
	// If that block fails, pipe the last thing into the failover block and return a Rejected out of that
	// If called on a Rejection or an uncaught Failure, pass it on through
	def andThenWithFailover[C](block: (A => C), failover: (A => B)): Failover[C, B] = this match {
		case Resolved(a) => {
			try {
				Resolved(block(a))
			} catch {
				case _: Throwable => try {
					// First block failed; ignore that exception and try the success value on the failover block instead
					Rejected(failover(a))
				} catch {
					case e: Throwable => {
						// Failover failed as well.  Throw that second exception.  First one is still lost forever
						Failed(e)
					}
				}
			}
		}
		case Failed(e) => Failed(e)
		case Rejected(b) => Rejected(b)
	}

	// If called on an uncaught failure, "catch" it and process it with the failover function
	// If called on a success or a failure that's already caught, pass it on through
	def andCatch(process: (Throwable => B)): Failover[A, B] = this match {
		case Resolved(a) => Resolved(a)
		case Failed(e) => try {
			Rejected(process(e))
		} catch {
			case e2: Throwable => Failed(e2)
		}
		case Rejected(_) => this
	}

	// If you need to run something no matter what happened, e.g. close database connections or something
	def andFinally(block: => Unit): Failover[A, B] = {
		block
		this
	}
}

case class Resolved[A, B](a: A) extends Failover[A, B] {
	def isResolved: Boolean = true

	def isFailed: Boolean = false

	def isRejected: Boolean = false
}

case class Failed[A, B](e: Throwable) extends Failover[A, B] {
	def isResolved: Boolean = false

	def isFailed: Boolean = true

	def isRejected: Boolean = false
}

case class Rejected[A, B](b: B) extends Failover[A, B] {
	def isResolved: Boolean = false

	def isFailed: Boolean = false

	def isRejected: Boolean = true
}

object Failover {
	def apply[A, B](block: => A): Failover[A, B] = {
		try {
			Resolved(block)
		} catch {
			case e: Throwable => Failed(e)
		}
	}

	def apply[A, B](block: => A, onFailure: (Throwable => B)): Failover[A, B] = {
		try {
			Resolved(block)
		} catch {
			case e: Throwable => Rejected(onFailure(e))
		}
	}
}
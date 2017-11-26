package CbiUtil

class Initializable[T] {
  var value: Option[T] = None

  def get: T = value match {
    case Some(t: T) => t
    case None => throw new Exception("Attempted to get() an uninitialized value")
  }

  def set(t: T): Unit = synchronized {
    value match {
      case None => value = Some(t)
      case Some(_) => throw new Exception("Attempted to set() an initialized value")
    }
  }

  def peek: Option[T] = value

  def isInitialized: Boolean = value.isDefined
}


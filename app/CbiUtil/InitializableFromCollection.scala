package CbiUtil

class InitializableFromCollection[T](find: (T => Boolean)) extends Initializable[T] {
  def setFromCollection(collection: Traversable[T]): T = value match {
    case None => {
      collection.find(find) match {
        case Some(t) => {
          value = Some(t)
          t
        }
        case None => throw new Exception("No match found in collection")
      }
    }
    case Some(t) => t
  }
}

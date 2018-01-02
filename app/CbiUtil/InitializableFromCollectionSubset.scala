package CbiUtil

class InitializableFromCollectionSubset[U <: Traversable[T], T](filter: (T => Boolean)) extends Initializable[U] {
  def findAllInCollection(collection: U): U = value match {
    case None => collection.filter(filter).asInstanceOf[U]
    case Some(t) => t
  }
}

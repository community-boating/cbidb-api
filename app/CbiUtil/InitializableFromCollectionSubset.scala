package CbiUtil

class InitializableFromCollectionSubset[U <: Traversable[T], T](filter: (T => Boolean)) extends Initializable[U] {
  def findAllInCollection(collection: U): U = value match {
    case None => {
      val ret = collection.filter(filter).asInstanceOf[U]
      value = Some(ret)
      ret
    }
    case Some(t) => t
  }
}

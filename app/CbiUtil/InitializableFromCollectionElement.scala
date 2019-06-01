package CbiUtil

class InitializableFromCollectionElement[T](find: (T => Boolean)) extends Initializable[T] {
	def findOneInCollection(collection: Traversable[T]): T = value match {
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

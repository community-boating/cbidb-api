package org.sailcbi.APIServer.CbiUtil

class DefinedInitializableNullary[T](initializer: () => T) extends Initializable[T] {
	def initialize(): T = set(initializer())

	// If it's not yet initialized, intialize.  Either way, return the value, don't throw.  Idempotent
	override def get(): T = synchronized {
		value match {
			case Some(t) => t
			case None => {
				value = Some(initializer())
				value.get
			}
		}
	}
}


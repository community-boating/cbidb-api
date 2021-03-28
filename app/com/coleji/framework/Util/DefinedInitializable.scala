package com.coleji.framework.Util

class DefinedInitializable[U, T](initializer: (U => T)) extends Initializable[T] {
	def initialize(input: U): T = set(initializer(input))

	// If it's not yet initialized, intialize.  Either way, return the value, don't throw.  Idempotent
	def getWithInput(input: U): T = synchronized {
		value match {
			case Some(t) => t
			case None => {
				value = Some(initializer(input))
				value.get
			}
		}
	}
}


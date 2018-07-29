package Entities.JsFacades.Stripe

import Entities.{CastableToStorableClass, CastableToStorableObject}

trait StripeCastableToStorableObject[T <: CastableToStorableClass] extends CastableToStorableObject[T] {
  val getURL: String
}

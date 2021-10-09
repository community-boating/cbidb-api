package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import com.coleji.neptune.Storable.{CastableToStorableClass, CastableToStorableObject}
import play.api.libs.json.JsValue

trait StripeCastableToStorableObject[T <: CastableToStorableClass] extends CastableToStorableObject[T] {
	def apply(v: JsValue): T

	val getURL: String
}

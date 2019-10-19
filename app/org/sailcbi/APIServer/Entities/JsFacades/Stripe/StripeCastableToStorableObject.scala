package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import org.sailcbi.APIServer.Entities.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.Services.Authentication.UserType
import play.api.libs.json.JsValue

trait StripeCastableToStorableObject[T <: CastableToStorableClass] extends CastableToStorableObject[T] {
	def apply(v: JsValue): T

	val getURL: String
}

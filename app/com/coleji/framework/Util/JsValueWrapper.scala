package com.coleji.framework.Util

import play.api.libs.json.{JsNull, JsValue}

case class JsValueWrapper (jsv: JsValue) {
	def getNonNull(prop: String): Option[JsValue] = try {
		jsv(prop) match {
			case JsNull => None
			case v: JsValue => Some(v)
		}

	} catch {
		case _: Throwable => None
	}
}

object JsValueWrapper {
	implicit def wrapJsValue(jsv: JsValue): JsValueWrapper = new JsValueWrapper(jsv)
}

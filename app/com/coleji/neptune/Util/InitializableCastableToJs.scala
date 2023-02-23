package com.coleji.neptune.Util

import play.api.libs.json.{JsValue, Json, Writes}

class InitializableCastableToJs[T](val cast: T => JsValue) extends Initializable[T] {

}

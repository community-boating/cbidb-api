package com.coleji.framework.IO.HTTP

sealed abstract class HTTPMethod

case object GET extends HTTPMethod {
	override def toString: String = "GET"
}

case object POST extends HTTPMethod {
	override def toString: String = "POST"
}

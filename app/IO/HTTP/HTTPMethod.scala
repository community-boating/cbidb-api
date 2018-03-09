package IO.HTTP

sealed abstract class HTTPMethod
case object GET extends HTTPMethod
case object POST extends HTTPMethod

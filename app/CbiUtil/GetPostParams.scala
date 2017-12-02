package CbiUtil

import play.api.mvc.{AnyContent, Request}

object GetPostParams {
  def apply(request: Request[AnyContent]): Option[Map[String, String]] = {
    request.body.asFormUrlEncoded match {
      case None => None
      case Some(v) =>
        Some(v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString("")))))
    }
  }
}

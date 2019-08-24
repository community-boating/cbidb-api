package org.sailcbi.APIServer.CbiUtil

import play.api.mvc.{AnyContent, Request}

object ValidateRequest {
	type ParameterSpec = (String, Boolean) // paramName, isRequired

	// None if the whole request is trash, else some map of usable param name -> value
	type RequestToParamsFunction = (Request[AnyContent] => Option[Map[String, String]])

	def post(allowedParams: Set[ParameterSpec]): RequestToParamsFunction =
		(request: Request[AnyContent]) => request.body.asFormUrlEncoded match {
			case None => None
			case Some(data) => {
				val allowedParamNames = allowedParams.map(_._1)

				val usableParams: Map[String, String] =
					data
							.filter(t => allowedParamNames contains t._1)
							.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))

				val requiredParams = allowedParams.filter(_._2).map(_._1)

				val atLeastOneRequiredParamNotProvided: Boolean =
					requiredParams.foldLeft(false)((failed: Boolean, required: String) => failed || !usableParams.contains(required))

				if (atLeastOneRequiredParamNotProvided) None
				else Some(usableParams)
			}
		}
}

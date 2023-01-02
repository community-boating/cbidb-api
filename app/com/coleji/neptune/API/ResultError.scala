package com.coleji.neptune.API

import play.api.libs.json.{JsObject, JsPath, Json, JsonValidationError}

case class ResultError (
	code: String,
	message: String
) {
	implicit val format = Json.format[ResultError]
	def asJsObject: JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

case class ParseError(
	code: String,
	message: String,
	parseErrors: Seq[(String, Seq[String])]
) {
	implicit val format = Json.format[ParseError]
	def asJsObject: JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

object ResultError {
	val UNAUTHORIZED = ResultError(
		code="access_denied",
		message="Authentication failure."
	)
	val UNKNOWN = ResultError(
		code="unknown",
		message="An unknown error occurred."
	)
	val NOT_JSON = ResultError(
		code="not_json",
		message="Post body not parsable as json."
	)
	val BAD_PARAMS = ResultError(
		code="bad_parameters",
		message="JSON parameters were not correct."
	)

	val PARSE_FAILURE: Seq[(JsPath, Seq[JsonValidationError])] => JsObject = (errors: Seq[(JsPath, Seq[JsonValidationError])]) => ParseError(
		code="parse_failure",
		message="Request was not valid.",
		parseErrors=errors.map(t => (t._1.toString(), t._2.flatMap(errs => errs.messages)))
	).asJsObject
}
package com.coleji.neptune.API

import play.api.libs.json.{JsObject, JsPath, Json, JsonValidationError}

case class ResultError (
	code: String,
	message: String
) {
	implicit val format = Json.format[ResultError]
	def asJsObject(): JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

case class ParseError(
	code: String,
	message: String,
	parseErrors: Seq[(String, Seq[String])]
) {
	implicit val format = Json.format[ParseError]
	def asJsObject(): JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

object ResultError {
	val UNAUTHORIZED: JsObject = ResultError(
		code="access_denied",
		message="Authentication failure."
	).asJsObject()
	val UNKNOWN: JsObject = ResultError(
		code="unknown",
		message="An unknown error occurred."
	).asJsObject()
	val NOT_JSON: JsObject = ResultError(
		code="not_json",
		message="Post body not parsable as json."
	).asJsObject()
	val BAD_PARAMS: JsObject = ResultError(
		code="bad_parameters",
		message="JSON parameters were not correct."
	).asJsObject()
	val PARSE_FAILURE: Seq[(JsPath, Seq[JsonValidationError])] => JsObject = (errors: Seq[(JsPath, Seq[JsonValidationError])]) => ParseError(
		code="parse_failure",
		message="Request was not valid.",
		parseErrors=errors.map(t => (t._1.toString(), t._2.flatMap(errs => errs.messages)))
	).asJsObject()
}
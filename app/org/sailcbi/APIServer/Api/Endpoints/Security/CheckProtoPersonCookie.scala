package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.{BouncerUserType, ProtoPersonUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckProtoPersonCookie @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		PA.withRequestCache(BouncerUserType)(None, ParsedRequest(request), rc => {
			val hasCookie = request.cookies.toSet.map((c: Cookie) => c.name).contains(ProtoPersonUserType.COOKIE_NAME)
			if (hasCookie) {
				// the request has a cookie...
				val cookie = request.cookies.get(ProtoPersonUserType.COOKIE_NAME).get.value
					try {
						PA.sleep()
						val pb = rc.pb
						val q = new PreparedQueryForSelect[Int](Set(BouncerUserType)) {
							override val params: List[String] = List(cookie)

							override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

							override def getQuery: String =
								s"""
								   |select person_id from persons
								   |where (proto_state is null or proto_state != '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}')
								   | and protoperson_cookie = ?
								   |""".stripMargin
						}
						val existingPersons = rc.executePreparedQueryForSelect(q)
						if (existingPersons.isEmpty) {
							// ... and the cookie is not attached to a non-proto user.  OK to keep using
							Future(Ok("Detected existing cookie"))
						} else {
							//... but the cookie is attached to a non-proto parent.  Make a new one
							setCookie(request, "Overriding stale cookie with new")
						}
					} catch {
						case _: Throwable => setCookie(request, "Error: Setting new cookie")
					}



			} else {
				setCookie(request, "Setting new cookie")
			}
		})

	}}

	def setCookie(request: Request[AnyContent], msg: String)(implicit exec: ExecutionContext): Future[Result] = {
		val headers = request.headers.toSimpleMap
		val secure = headers.get("BOUNCER_SECURE_TO_USE").map(_ != "false").getOrElse(
			headers.get("Outside-Connection-HTTPS").map(_ != "false").getOrElse(true)
		)
		val cookie = Cookie(
			name = ProtoPersonUserType.COOKIE_NAME,
			value = ProtoPersonUserType.COOKIE_VALUE_PREFIX + scala.util.Random.alphanumeric.take(30).mkString,
			maxAge = Some(60 * 60 * 24 * 3), // 3 days
			secure = secure,
			httpOnly = true,
			sameSite = Some(Cookie.SameSite.Lax)
		)
		Future(Ok(msg).withCookies(cookie))
	}
}
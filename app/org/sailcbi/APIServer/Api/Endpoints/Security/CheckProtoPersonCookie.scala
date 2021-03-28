package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.{BouncerRequestCache, ProtoPersonRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckProtoPersonCookie @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		PA.withRequestCache(BouncerRequestCache)(None, ParsedRequest(request), rc => {
			val hasCookie = request.cookies.toSet.map((c: Cookie) => c.name).contains(ProtoPersonRequestCache.COOKIE_NAME)
			if (hasCookie) {
				// the request has a cookie...
				val cookie = request.cookies.get(ProtoPersonRequestCache.COOKIE_NAME).get.value
					try {
						PA.sleep()

						val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
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
			name = ProtoPersonRequestCache.COOKIE_NAME,
			value = ProtoPersonRequestCache.COOKIE_VALUE_PREFIX + scala.util.Random.alphanumeric.take(30).mkString,
			maxAge = Some(60 * 60 * 24 * 3), // 3 days
			secure = secure,
			httpOnly = true,
			sameSite = Some(Cookie.SameSite.Lax)
		)
		Future(Ok(msg).withCookies(cookie))
	}
}
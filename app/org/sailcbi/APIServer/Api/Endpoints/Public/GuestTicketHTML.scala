package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuestTicketHTML @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(cardNumber: Int, nonce: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(PublicRequestCache)(None, ParsedRequest(request), rc => {
			PortalLogic.getTicketBarcodeBase64(rc, cardNumber, nonce) match {
				case Some(img) => {
					val bos = new ByteArrayOutputStream()
					ImageIO.write(img, "png", bos)
					val ret = bos.toByteArray
					bos.close()
					Future(Ok(ret).as("image/jpeg"))
				}
				case None => Future(Ok(""))
			}

		})
	}
}

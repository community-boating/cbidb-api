package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassType
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JpClassTypes @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val classTypes = rc.getAllObjectsOfClass(JpClassType, Set(
				JpClassType.fields.typeId,
				JpClassType.fields.typeName,
				JpClassType.fields.displayOrder,
				JpClassType.fields.sessionLength,
			))
			Future(Ok(Json.toJson(classTypes)))
		})
	})
}
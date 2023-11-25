package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassTypes.{DtoStaffRestApClassTypesGetResponseSuccess, DtoStaffRestApClassTypesGetResponseSuccess_ApClassFormats}
import org.sailcbi.APIServer.Entities.EntityDefinitions.BoatType
import org.sailcbi.APIServer.Entities.cacheable.ApClassTypes
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassTypes @Inject()(implicit val exec: ExecutionContext) extends RestController(BoatType) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val types = ApClassTypes.get(rc, null)._1
			implicit val writes: OFormat[DtoStaffRestApClassTypesGetResponseSuccess] = DtoStaffRestApClassTypesGetResponseSuccess.format
			val ret = types.map(t => new DtoStaffRestApClassTypesGetResponseSuccess(
				typeId = t.values.typeId.get,
				typeName = t.values.typeName.get,
				ratingPrereq = t.values.ratingPrereq.get,
				classPrereq = t.values.classPrereq.get,
				ratingOverkill = t.values.ratingOverkill.get,
				displayOrder = t.values.displayOrder.get,
				descLong = t.values.descLong.get,
				descShort = t.values.descShort.get,
				classOverkill = t.values.classOverkill.get,
				noSignup = t.values.noSignup.get,
				priceDefault = t.values.priceDefault.get,
				signupMaxDefault = t.values.signupMaxDefault.get,
				signupMinDefault = t.values.signupMinDefault.get,
				disallowIfOverkill = t.values.disallowIfOverkill.get,
				$$apClassFormats = t.references.apClassFormats.peek.map(fs => fs.map(f => new DtoStaffRestApClassTypesGetResponseSuccess_ApClassFormats(
					formatId = f.values.formatId.get,
					typeId = f.values.typeId.get,
					description = f.values.description.get,
					priceDefaultOverride = f.values.priceDefaultOverride.get,
					sessionCtDefault = f.values.sessionCtDefault.get,
					sessionLengthDefault = f.values.sessionLengthDefault.get,
					signupMaxDefaultOverride = f.values.signupMaxDefaultOverride.get,
					signupMinDefaultOverride = f.values.signupMinDefaultOverride.get
				)).toList).getOrElse(List.empty)
			)).toList
			Future(Ok(Json.toJson(ret)))
		})
	})
}
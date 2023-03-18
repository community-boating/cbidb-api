package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassType}
import play.api.libs.json.Json

import java.time.Duration

object ApClassTypes extends CacheableFactory[Null, IndexedSeq[ApClassType]] {
	override protected val lifetime: Duration = Duration.ofMinutes(1)

	override protected def calculateKey(config: Null): String = CacheKeys.apClassTypes

	override protected def generateResult(rc: RequestCache, config: Null): IndexedSeq[ApClassType] = {
		getObjects(rc.assertUnlocked)
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[ApClassType] = {
		val types = rc.getAllObjectsOfClass(ApClassType, Set(
			ApClassType.fields.typeId,
			ApClassType.fields.typeName,
			ApClassType.fields.ratingPrereq,
			ApClassType.fields.classPrereq,
			ApClassType.fields.ratingOverkill,
			ApClassType.fields.displayOrder,
			ApClassType.fields.descShort,
			ApClassType.fields.descLong,
			ApClassType.fields.classOverkill,
			ApClassType.fields.noSignup,
			ApClassType.fields.priceDefault,
			ApClassType.fields.signupMinDefault,
			ApClassType.fields.signupMaxDefault,
			ApClassType.fields.disallowIfOverkill,
		))

		val formats = rc.getAllObjectsOfClass(ApClassFormat, Set(
			ApClassFormat.fields.formatId,
			ApClassFormat.fields.typeId,
			ApClassFormat.fields.description,
			ApClassFormat.fields.signupMinDefaultOverride,
			ApClassFormat.fields.signupMaxDefaultOverride,
			ApClassFormat.fields.sessionCtDefault,
			ApClassFormat.fields.sessionLengthDefault,
			ApClassFormat.fields.priceDefaultOverride,
		))

		types.foreach(t => {
			val matchingFormats = formats.filter(f => f.values.typeId.get == t.values.typeId.get)
			t.references.apClassFormats.set(matchingFormats.toIndexedSeq)
		})

		types.toIndexedSeq
	}
}

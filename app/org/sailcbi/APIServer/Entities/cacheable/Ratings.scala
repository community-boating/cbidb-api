package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatRating, Rating, RatingProgram}
import play.api.libs.json.Json

import java.time.Duration

object Ratings extends CacheableFactory[Null, String]{
	override protected val lifetime: Duration = Duration.ofMinutes(1)

	override protected def calculateKey(config: Null): String = CacheKeys.ratings

	override protected def generateResult(rc: RequestCache, config: Null): String = {
		Json.toJson(getObjects(rc.assertUnlocked)).toString
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): List[Rating] = {
		val ratings = rc.getAllObjectsOfClass(Rating, Set(
			Rating.fields.ratingId,
			Rating.fields.ratingName,
			Rating.fields.overriddenBy,
		))
		val boatsRatings = rc.getAllObjectsOfClass(BoatRating, Set(
			BoatRating.fields.assignId,
			BoatRating.fields.boatId,
			BoatRating.fields.programId,
			BoatRating.fields.flag,
			BoatRating.fields.ratingId,
		))
		val ratingsPrograms = rc.getAllObjectsOfClass(RatingProgram, Set(
			RatingProgram.fields.assignId,
			RatingProgram.fields.ratingId,
			RatingProgram.fields.programId,
		))

		ratings.foreach(r => {
			val ratingId = r.values.ratingId.get
			r.references.boats.set(boatsRatings.filter(_.values.ratingId.get == ratingId))
			r.references.programs.set(ratingsPrograms.filter(_.values.ratingId.get == ratingId))
		})
		ratings
	}
}

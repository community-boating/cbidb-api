package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, NullableIntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, NullableIntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable.{FieldsObject, ReferencesObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.framework.Storable._

class Rating extends StorableClass {
	this.setCompanion(Rating)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val ratingId = new IntFieldValue(self, Rating.fields.ratingId)
		val ratingName = new StringFieldValue(self, Rating.fields.ratingName)
		val overriddenBy = new NullableIntFieldValue(self, Rating.fields.overriddenBy)
	}

	override val valuesList = List(
		values.ratingId,
		values.ratingName,
		values.overriddenBy
	)
}

object Rating extends StorableObject[Rating] {
	val entityName: String = "RATINGS"

	object fields extends FieldsObject {
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val ratingName = new StringDatabaseField(self, "RATING_NAME", 100)
		val overriddenBy = new NullableIntDatabaseField(self, "OVERRIDDEN_BY")
	}

	def primaryKey: IntDatabaseField = fields.ratingId

	object specialIDs {
		val RATING_ID_KAYAK: Int = 4
		val RATING_ID_SUP: Int = 28
		val RATING_ID_RIGGING: Int = 61
		val RATING_ID_VERBAL: Int = 62
		val RATING_ID_MERC_GREEN: Int = 261
	}

	// TODO: move to logic
	def getAllHigherRatingsThanRating(ratings: List[Rating], targetRatingId: Int): List[Rating] = {
		val targetRating: Rating = ratings.filter(_.values.ratingId.get == targetRatingId).head
		targetRating.values.overriddenBy.get match {
			case Some(i: Int) => targetRating :: getAllHigherRatingsThanRating(ratings, i)
			case None => targetRating :: Nil
		}
	}

	def ratingIsUsableWithMembership(ratingId: Int, membershipTypeId: Int): Boolean = membershipTypeId match {
		case MembershipType.specialIDs.MEM_TYPE_ID_30_DAY => ratingId match {
			case Rating.specialIDs.RATING_ID_KAYAK => true
			case Rating.specialIDs.RATING_ID_SUP => true
			case Rating.specialIDs.RATING_ID_RIGGING => true
			case Rating.specialIDs.RATING_ID_VERBAL => true
			case Rating.specialIDs.RATING_ID_MERC_GREEN => true
			case _ => false
		}
		case _ => true
	}
}
package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SignoutSnapshot extends StorableClass(SignoutSnapshot) {
	override object values extends ValuesObject {
		val snapshotDatetime = new DateTimeFieldValue(self, SignoutSnapshot.fields.snapshotDatetime)
		val programId = new IntFieldValue(self, SignoutSnapshot.fields.programId)
		val boatId = new IntFieldValue(self, SignoutSnapshot.fields.boatId)
		val flag = new NullableStringFieldValue(self, SignoutSnapshot.fields.flag)
		val signoutCount = new DoubleFieldValue(self, SignoutSnapshot.fields.signoutCount)
		val pruneDupes = new NullableStringFieldValue(self, SignoutSnapshot.fields.pruneDupes)
		val definiteCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.definiteCount)
		val probableCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.probableCount)
		val maybeCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.maybeCount)
		val probablyNotCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.probablyNotCount)
		val pinkDefiniteCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.pinkDefiniteCount)
		val pinkProbableCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.pinkProbableCount)
		val pinkMaybeCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.pinkMaybeCount)
		val pinkProbablyNotCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.pinkProbablyNotCount)
		val pinkSignoutCount = new NullableDoubleFieldValue(self, SignoutSnapshot.fields.pinkSignoutCount)
	}
}

object SignoutSnapshot extends StorableObject[SignoutSnapshot] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SIGNOUT_SNAPSHOTS"

	object fields extends FieldsObject {
		val snapshotDatetime = new DateTimeDatabaseField(self, "SNAPSHOT_DATETIME")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val flag = new NullableStringDatabaseField(self, "FLAG", 1)
		val signoutCount = new DoubleDatabaseField(self, "SIGNOUT_COUNT")
		val pruneDupes = new NullableStringDatabaseField(self, "PRUNE_DUPES", 1)
		val definiteCount = new NullableDoubleDatabaseField(self, "DEFINITE_COUNT")
		val probableCount = new NullableDoubleDatabaseField(self, "PROBABLE_COUNT")
		val maybeCount = new NullableDoubleDatabaseField(self, "MAYBE_COUNT")
		val probablyNotCount = new NullableDoubleDatabaseField(self, "PROBABLY_NOT_COUNT")
		val pinkDefiniteCount = new NullableDoubleDatabaseField(self, "PINK_DEFINITE_COUNT")
		val pinkProbableCount = new NullableDoubleDatabaseField(self, "PINK_PROBABLE_COUNT")
		val pinkMaybeCount = new NullableDoubleDatabaseField(self, "PINK_MAYBE_COUNT")
		val pinkProbablyNotCount = new NullableDoubleDatabaseField(self, "PINK_PROBABLY_NOT_COUNT")
		val pinkSignoutCount = new NullableDoubleDatabaseField(self, "PINK_SIGNOUT_COUNT")
	}

	def primaryKey: IntDatabaseField = fields.snapshotDatetime
}
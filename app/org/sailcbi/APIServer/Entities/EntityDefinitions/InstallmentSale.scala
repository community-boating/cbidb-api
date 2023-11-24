package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class InstallmentSale extends StorableClass(InstallmentSale) {
	override object values extends ValuesObject {
		val saleId = new IntFieldValue(self, InstallmentSale.fields.saleId)
		val amountInCents = new DoubleFieldValue(self, InstallmentSale.fields.amountInCents)
		val staggerId = new IntFieldValue(self, InstallmentSale.fields.staggerId)
		val closeId = new IntFieldValue(self, InstallmentSale.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, InstallmentSale.fields.voidCloseId)
	}
}

object InstallmentSale extends StorableObject[InstallmentSale] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "INSTALLMENT_SALES"

	object fields extends FieldsObject {
		val saleId = new IntDatabaseField(self, "SALE_ID")
		val amountInCents = new DoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
	}

	def primaryKey: IntDatabaseField = fields.saleId
}
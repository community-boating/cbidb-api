package org.sailcbi.APIServer.Entities

// As more things get converted into captial-E org.sailcbi.APIServer.Entities, move these things into the corresponding entities
object MagicIds {
	val FO_ITEM_TAX_DISCREPANCIES: Int = 661
	val FO_AR_SOURCE_SAGE: Int = 6
	val FO_INPERSON_CC_SOURCE_IDs: List[Int] = List(1, 2, 581, 582, 701, 702)
	val JUNIOR_SUMMER_MEMBERSHIP_TYPE_ID: Int = 10
	val PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK = 2
	object PERSONS_PROTO_STATE {
		val IS_PROTO = "I"
		val WAS_PROTO = "W"
	}
	object PERSON_TYPE {
		val JP_PARENT = 41
	}
	object DISCOUNTS {
		val RENEWAL_DISCOUNT_ID = 8
		val YOUTH_DISCOUNT_ID = 2
		val SENIOR_DISCOUNT_ID = 1
		val STUDENT_DISCOUNT_ID = 3
		val MGH_DISCOUNT_ID = 7
		val VETERAN_DISCOUNT_ID = 442
	}
	object MEMBERSHIP_TYPES {
		val FULL_YEAR_TYPE_ID = 1
		val FULL_YEAR_PADDLING_TYPE_ID = 882
	}
	object PROGRAM_TYPES {
		val ADULT_PROGRAM_ID = 1
	}

	object ORDER_NUMBER_APP_ALIAS {
		val AP = "AP"
		val JP = "JP"
		val SHARED = "Shared"
		val DONATE = "Donate"
		val GC = "GC"
	}
	val MIN_AGE_FOR_SENIOR = 65
	val MAX_AGE_FOR_YOUTH = 20
	object PW_HASH_SCHEME {
		val MEMBER_1 = "member_01"
		val MEMBER_2 = "member_02"
		val staff_1 = "staff_01"
		val staff_2 = "staff_02"
	}
}

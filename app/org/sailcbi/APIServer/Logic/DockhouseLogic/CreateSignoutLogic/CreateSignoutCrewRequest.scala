package org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic

case class CreateSignoutCrewRequest(
	personId: Int,
	cardNumber: String,
	testRatingId: Option[Int]
)
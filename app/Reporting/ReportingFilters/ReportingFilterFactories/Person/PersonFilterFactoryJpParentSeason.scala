package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities._
import Reporting.ReportingFilters._
import Services.PersistenceBroker
import Services.ServerStateWrapper.ss

class PersonFilterFactoryJpParentSeason extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN)
  val displayName: String = "JP Parent in Season"
  val defaultValue: String = ss.currentSeason().toString
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val season = arg.toInt

    val juniorsThatYear = pb.getObjectsByFilters(
      PersonMembership,
      List(
        PersonMembership.fields.membershipTypeId.equalsConstant(MembershipType.specialIDs.MEM_TYPE_ID_JUNIOR_SUMMER),
        PersonMembership.fields.expirationDate.isYearConstant(season)
      )
    ).map(_.values.personId.get)

    val parentIds = pb.getObjectsByFilters(
      PersonRelationship,
      List(
        PersonRelationship.fields.typeId.equalsConstant(PersonRelationship.specialIDs.TYPE_ID_PARENT_CHILD_ACCT_LINKED),
        PersonRelationship.fields.b.inList(juniorsThatYear)
      )
    ).map(_.values.a.get)

    pb.getObjectsByIds(Person, parentIds).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val GO_BACK = 5
    val currentSeason = ss.currentSeason()
    val start = currentSeason - GO_BACK
    List(
      (start to currentSeason).toList.map(_.toString).map(i => (i, i))
    )
  }
}
package Reporting.ReportingFilters.ReportingFilterFactories.Person


import Entities.EntityDefinitions._
import Logic.DateLogic
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryJpParentSeason extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val displayName: String = "JP Parent in Season"
  val argDefinitions = List(
    (ARG_DROPDOWN, DateLogic.currentSeason().toString)
  )
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
    val currentSeason = DateLogic.currentSeason()
    val start = currentSeason - GO_BACK
    List(
      (start to currentSeason).toList.map(_.toString).map(i => (i, i))
    )
  }
}

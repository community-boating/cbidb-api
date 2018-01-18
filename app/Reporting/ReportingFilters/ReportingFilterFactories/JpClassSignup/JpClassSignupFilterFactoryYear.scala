package Reporting.ReportingFilters.ReportingFilterFactories.JpClassSignup

import Entities.EntityDefinitions.{JpClassSession, JpClassSignup}
import Logic.DateLogic
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class JpClassSignupFilterFactoryYear extends ReportingFilterFactory[JpClassSignup] {
  val displayName: String = "By Season"
  val argDefinitions = List(
    (ARG_INT, DateLogic.currentSeason().toString)
  )
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[JpClassSignup] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    val year = arg.toInt
    implicit val pb: PersistenceBroker = _pb
    val sessions = pb.getObjectsByFilters(
      JpClassSession,
      List(JpClassSession.fields.sessionDateTime.isYearConstant(year)),
      100
    )
    val instanceIDs = sessions.map(_.values.instanceId.get).toSet
    pb.getObjectsByFilters(
      JpClassSignup,
      List(JpClassSignup.fields.instanceId.inList(instanceIDs.toList)),
      100
    ).toSet
  })
}

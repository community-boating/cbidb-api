package org.sailcbi.APIServer.Logic.DockhouseLogic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions._

object DockReportLogic {
	def getSubobjects(dockReport: DockReport, rc: UnlockedRequestCache): (List[DockReportWeather], List[DockReportStaff], List[DockReportUapAppt], List[DockReportHullCount], List[DockReportApClass]) ={
		val weather = rc.getObjectsByFilters(DockReportWeather, List(DockReportWeather.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportWeather.fields.weatherId,
			DockReportWeather.fields.dockReportId,
			DockReportWeather.fields.weatherDatetime,
			DockReportWeather.fields.temp,
			DockReportWeather.fields.weatherSummary,
			DockReportWeather.fields.windDir,
			DockReportWeather.fields.windSpeedKtsSteady,
			DockReportWeather.fields.windSpeedKtsGust,
			DockReportWeather.fields.restrictions,
		))
		val dockstaff = rc.getObjectsByFilters(DockReportStaff, List(DockReportStaff.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportStaff.fields.dockReportStaffId,
			DockReportStaff.fields.dockReportId,
			DockReportStaff.fields.dockmasterOnDuty,
			DockReportStaff.fields.staffName,
			DockReportStaff.fields.timeIn,
			DockReportStaff.fields.timeOut,
		))
		val uapAppts = rc.getObjectsByFilters(DockReportUapAppt, List(DockReportUapAppt.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportUapAppt.fields.dockReportApptId,
			DockReportUapAppt.fields.dockReportId,
			DockReportUapAppt.fields.apptDatetime,
			DockReportUapAppt.fields.apptType,
			DockReportUapAppt.fields.participantName,
			DockReportUapAppt.fields.boatTypeId,
			DockReportUapAppt.fields.instructorName,
			DockReportUapAppt.fields.hoyer,
		))
		val hullCounts = rc.getObjectsByFilters(DockReportHullCount, List(DockReportHullCount.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportHullCount.fields.dockReportHullCtId,
			DockReportHullCount.fields.dockReportId,
			DockReportHullCount.fields.hullType,
			DockReportHullCount.fields.inService,
			DockReportHullCount.fields.staffTally,
		))
		val apClasses = rc.getObjectsByFilters(DockReportApClass, List(DockReportApClass.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportApClass.fields.dockReportApClassId,
			DockReportApClass.fields.dockReportId,
			DockReportApClass.fields.apInstanceId,
			DockReportApClass.fields.className,
			DockReportApClass.fields.classDatetime,
			DockReportApClass.fields.location,
			DockReportApClass.fields.instructor,
			DockReportApClass.fields.attend,
		))
		(weather, dockstaff, uapAppts, hullCounts, apClasses)
	}

	def deleteSubobjects(dockReport: DockReport, rc: UnlockedRequestCache): Unit = {
		val (weather, dockstaff, uapAppts, hullCounts, apClasses) = getSubobjects(dockReport, rc)
		val weatherIds = weather.map(_.values.weatherId.get)
		val dockstaffIds = dockstaff.map(_.values.dockReportStaffId.get)
		val uapApptIds = uapAppts.map(_.values.dockReportApptId.get)
		val hullCountIds = hullCounts.map(_.values.dockReportHullCtId.get)
		val apClassIds = apClasses.map(_.values.dockReportApClassId.get)
		rc.deleteObjectsById(DockReportWeather, weatherIds)
		rc.deleteObjectsById(DockReportStaff, dockstaffIds)
		rc.deleteObjectsById(DockReportUapAppt, uapApptIds)
		rc.deleteObjectsById(DockReportHullCount, hullCountIds)
		rc.deleteObjectsById(DockReportApClass, apClassIds)
	}
}

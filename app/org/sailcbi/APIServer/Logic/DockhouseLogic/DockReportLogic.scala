package org.sailcbi.APIServer.Logic.DockhouseLogic

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.dto.PutDockReportApClassDto

import java.time.LocalDate

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
		val apClasses = getDockReportClasses(rc, dockReport)
		(weather, dockstaff, uapAppts, hullCounts, apClasses)
	}

	def getDockReportClasses(rc: UnlockedRequestCache, dockReport: DockReport): List[DockReportApClass] = {
		rc.getObjectsByFilters(DockReportApClass, List(DockReportApClass.fields.dockReportId.alias.equalsConstant(dockReport.values.dockReportId.get)), Set(
			DockReportApClass.fields.dockReportApClassId,
			DockReportApClass.fields.dockReportId,
			DockReportApClass.fields.apInstanceId,
			DockReportApClass.fields.className,
			DockReportApClass.fields.classDatetime,
			DockReportApClass.fields.location,
			DockReportApClass.fields.instructor,
			DockReportApClass.fields.attend,
		))
	}

	def refreshDockReportClasses(rc: UnlockedRequestCache, dockReport: DockReport, findOrMakeDockReportClass: Int => DockReportApClass): List[PutDockReportApClassDto] = {
		val apClassesQb = QueryBuilder
			.from(ApClassSession)
			.innerJoin(ApClassInstance, ApClassInstance.fields.instanceId.alias equalsField  ApClassSession.fields.instanceId.alias)
			.innerJoin(ApClassFormat, ApClassFormat.fields.formatId.alias equalsField ApClassInstance.fields.formatId.alias)
			.innerJoin(ApClassType, ApClassType.fields.typeId.alias equalsField ApClassFormat.fields.typeId.alias)
			.outerJoin(Person.aliasOuter, Person.fields.personId.alias equalsField ApClassInstance.fields.instructorId.alias)
			.where(ApClassSession.fields.sessionDatetime.alias isDateConstant LocalDate.now())
			.select(List(
				ApClassType.fields.typeName.alias,
				ApClassInstance.fields.instanceId.alias,
				ApClassSession.fields.sessionDatetime.alias,
				ApClassInstance.fields.locationString.alias,
				Person.fields.nameFirst.alias,
				Person.fields.nameLast.alias
			))

		val apClassesQbrrs = rc.executeQueryBuilder(apClassesQb)

		val dockReportClasses = apClassesQbrrs.map(qbrr => {
			val dockRptClass = findOrMakeDockReportClass(qbrr.getValue(ApClassInstance.alias)(_.instanceId))
			dockRptClass.values.dockReportId.update(dockReport.getID)
			dockRptClass.values.className.update(qbrr.getValue(ApClassType.alias)(_.typeName))
			dockRptClass.values.classDatetime.update(qbrr.getValue(ApClassSession.alias)(_.sessionDatetime))
			dockRptClass.values.apInstanceId.update(qbrr.getValue(ApClassInstance.alias)(_.instanceId))
			dockRptClass.values.location.update(qbrr.getValue(ApClassInstance.alias)(_.locationString))
			dockRptClass.values.instructor.update(qbrr.getValue(Person.alias)(_.nameFirst)
				.map(v => v + qbrr.getValue(Person.alias)(_.nameLast)
					.map(v2 => " " + v2).getOrElse("")))
			rc.commitObjectToDatabase(dockRptClass)
			dockRptClass.defaultAllUnsetNullableFields()
			PutDockReportApClassDto(dockRptClass)
		})

		dockReportClasses

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

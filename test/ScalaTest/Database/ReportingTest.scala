package ScalaTest.Database

import Api.Endpoints.ReportingAPI.GetReportRunOptions
import Entities.EntityDefinitions.{JpClassInstance, JpClassType}
import Reporting.{Report, ReportFactory}
import Reporting.ReportingFilters.{ARG_DATE, ARG_DOUBLE, ARG_DROPDOWN, ARG_INT, ReportingFilterFactoryDropdown}
import ScalaTest.GetPersistenceBroker
import Services.{ServerBootLoader, ServerStateContainer}
import Storable.StorableClass
import Storable.StorableQuery.{ColumnAlias, JoinPoint, QueryBuilder, TableAlias}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{JsArray, JsBoolean, JsObject, JsString}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class ReportingTest extends FunSuite{
	//	test("Test db connection") {
	//		GetPersistenceBroker { pb =>
	//			val firstClass = pb.getObjectById(JpClassType, 1)
	//			val name = (firstClass.orNull.values.typeName.get)
	//			assert(name == ("Learn to Sail"))
	//		}
	//	}

	test("Test query1") {
		println("hi")
		ServerBootLoader.setSSC(ServerStateContainer(
			serverTimeOffsetSeconds = 0
		))
		implicit val ec = ExecutionContext.global
		GetPersistenceBroker { pb =>
			case class FilterDataForJSON(
												filterName: String,
												displayName: String,
												filterType: String,
												defaultValue: String,
												dropdownValues: Option[List[List[(String, String)]]]
										)
			val resultData: JsArray = Report.reportFactoryMap.foldLeft(new JsArray)((arr, e) => {
				val entityName: String = e._1
				val entityDisplayName: String = e._2._1

				val factoryInstance: ReportFactory[_ <: StorableClass] =
					Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

				// Field name and display name
				val fieldData: List[(String, String, Boolean)] = factoryInstance.fieldList.map(f => (f._1, f._2.fieldDisplayName, f._2.isDefault))
				val filterData: List[FilterDataForJSON] =
					factoryInstance.filterList.map(f => FilterDataForJSON(
						f._1,
						f._2.displayName,
						f._2.argDefinitions.map(_._1).map({
							case ARG_INT => "Int"
							case ARG_DOUBLE => "Double"
							case ARG_DATE => "Date"
							case ARG_DROPDOWN => "Dropdown"
							case t: Any => throw new Exception("Unconfigured arg type " + t)
						}).mkString(","),
						f._2.argDefinitions.map(_._2).mkString(","),
						f._2 match {
							case d: ReportingFilterFactoryDropdown => None
							case _ => None
						}
					)).toList

				arr append JsObject(Map(
					"entityName" -> JsString(entityName),
					"displayName" -> JsString(entityDisplayName),
					"fieldData" -> fieldData.foldLeft(new JsArray)((arr, t) => {
						arr append JsObject(Map(
							"fieldName" -> JsString(t._1),
							"fieldDisplayName" -> JsString(t._2),
							"isDefault" -> JsBoolean(t._3)
						))
					}),
					"filterData" -> filterData.foldLeft(new JsArray)((arr, t) => {
						arr append JsObject(Map(
							"filterName" -> JsString(t.filterName),
							"displayName" -> JsString(t.displayName),
							"filterType" -> JsString(t.filterType),
							"default" -> JsString(t.defaultValue),
							"values" -> (t.dropdownValues match {
								case Some(ll: List[List[(String, String)]]) => JsArray(ll.map(l => JsArray(l.map(v => JsObject(Map(
									"display" -> JsString(v._2),
									"return" -> JsString(v._1)
								))))))
								case _ => JsArray()
							})
						))
					})
				))
			})
			println(resultData)
		}
	}


}

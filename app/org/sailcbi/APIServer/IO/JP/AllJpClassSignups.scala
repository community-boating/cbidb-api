package org.sailcbi.APIServer.IO.JP

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object AllJpClassSignups {
	def get(rc: StaffRequestCache, instanceIds: List[Int])(implicit PA: PermissionsAuthority): List[JpClassSignup] = {
		val types = TableAlias.wrapForInnerJoin(JpClassType)
		val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
		val signups = TableAlias.wrapForInnerJoin(JpClassSignup)
		val persons = TableAlias.wrapForInnerJoin(Person)

		val groups = TableAlias.wrapForOuterJoin(JpGroup)
		val wlResults = TableAlias.wrapForOuterJoin(JpClassWlResult)
		val sections = TableAlias.wrapForOuterJoin(JpClassSection)
		val sectionLookups = TableAlias.wrapForOuterJoin(JpClassSectionLookup)

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, types.wrappedFields(_.fields.typeId).wrapFilter(_.equalsField(instances.wrappedFields(_.fields.typeId))))
			.innerJoin(signups, instances.wrappedFields(_.fields.instanceId).wrapFilter(_.equalsField(signups.wrappedFields(_.fields.instanceId))))
			.innerJoin(persons, signups.wrappedFields(_.fields.personId).wrapFilter(_.equalsField(persons.wrappedFields(_.fields.personId))))

			.outerJoin(groups, signups.wrappedFields(_.fields.groupId).wrapFilter(_.equalsField(groups.wrappedFields(_.fields.groupId))))
			.outerJoin(wlResults, signups.wrappedFields(_.fields.signupId).wrapFilter(_.equalsField(wlResults.wrappedFields(_.fields.signupId))))
			.outerJoin(sections, signups.wrappedFields(_.fields.sectionId).wrapFilter(_.equalsField(sections.wrappedFields(_.fields.sectionId))))
			.outerJoin(sectionLookups, sections.wrappedFields(_.fields.lookupId).wrapFilter(_.equalsField(sectionLookups.wrappedFields(_.fields.sectionId))))

			.where(instances.wrappedFields(_.fields.instanceId).wrapFilter(_.inList(instanceIds)))
			// .where(instances.wrappedFields(_.fields.instanceId).wrapFilter(_.equalsConstant(5361)))

			.select(
				QueryBuilder.allFieldsFromTable(types) ++
				QueryBuilder.allFieldsFromTable(instances) ++
				QueryBuilder.allFieldsFromTable(signups) ++
				QueryBuilder.allFieldsFromTable(wlResults) ++
				QueryBuilder.allFieldsFromTable(groups) ++
				QueryBuilder.allFieldsFromTable(sections) ++
				QueryBuilder.allFieldsFromTable(sectionLookups) ++
				List(
					persons.wrappedFields(_.fields.personId),
					persons.wrappedFields(_.fields.nameFirst),
					persons.wrappedFields(_.fields.nameLast),
				)
			)

		val allSignups = rc.executeQueryBuilder(sessionsQB).map(qbrr => {
			val signup = signups.construct(qbrr)
			val instance = instances.construct(qbrr)
			val classType = types.construct(qbrr)
			val wlResult = wlResults.construct(qbrr)
			val group = groups.construct(qbrr)
			val person = persons.construct(qbrr)
			val section = sections.construct(qbrr)
			val sectionLookup = sectionLookups.construct(qbrr)

			signup.references.jpClassWlResult.set(wlResult)
			signup.references.jpClassInstance.set(instance)
			signup.references.group.set(group)
			signup.references.person.set(person)
			signup.references.section.set(section)
			section match {
				case Some(s) => s.references.sectionLookup.set(sectionLookup.get)
				case None =>
			}
			instance.references.jpClassType.set(classType)
			signup
		})
		allSignups
	}
}

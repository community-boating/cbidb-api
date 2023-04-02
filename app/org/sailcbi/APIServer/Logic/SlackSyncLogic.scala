package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Api.Endpoints.Staff.slacksyncusers.{SlackSyncResponse, SlackUserDto}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Person

object SlackSyncLogic {
	def processSlackUserSync(rc: UnlockedRequestCache, slackUsers: List[SlackUserDto]): SlackSyncResponse = {
		// look up all persons by email
		val personsMatchingEmails = rc.getObjectsByFilters(
			Person,
			List(Person.fields.email.alias.inListLowercase(slackUsers.map(_.email))),
			Set(
				Person.fields.personId,
				Person.fields.nameFirst,
				Person.fields.nameLast,
				Person.fields.email
			)
		)

		// look up all persons by slack userid
		val personsMatchingSlackUserid = rc.getObjectsByFilters(
			Person,
			List(Person.fields.slackUserId.alias.inListLowercase(slackUsers.map(_.userid))),
			Set(
				Person.fields.personId,
				Person.fields.nameFirst,
				Person.fields.nameLast,
				Person.fields.email
			)
		)

		// put each userId from the slack import into one of the following buckets:
		// A) There is exactly one person record that matches both email and slack userid
		//      -> Great, no changes needed
		// B) There is exactly one person record that matches the slack userid, but the email is different
		//      -> (Info) We'll assume its the same person.  No changes, but tell the user that the emails dont match
		// C) There are multiple records that match the slack userId.  This should not be possible
		//      -> panic
		// D) There is exactly one person record that matches the email, no matches on slack userid
		//      -> (Info) Go ahead and set the slackId on that record.  Add a message saying new slack userid association created
		// E) There are multiple records that match the email, no matches on slack userid
		//      -> (Warn) Tell the user that we can't be sure who it is.  User should either dedupe the records,
		//         or if its legitimately multiple people that share an email address,
		//         the user shoudl email jon to manually set the slackId on the right one.  Send a non-fatal crash email
		// F) There are no records that match either email or slack userId
		//      -> (Warn) Tell the user we can't find this person, and they should probably be removed from slack
		// G) Some other bucket I didn't think of
		//      -> panic


		// For all users in buckets A, B, D, check if the user should be removed from slack
		// AA) They are a current member in good standing
		//     -> No changes needed
		// BB) They are banned from slack
		//      -> (Critical) they should be removed ASAP
		// CC) They have an expired AP membership but no active membership
		//      -> (Warn) They should maybe be removed
		// DD) THey have no AP membership, past or present
		//       -> (Warn) They should be removed

		SlackSyncResponse(
			users = slackUsers,
			userStatus = List(),
			messages = List()
		)
	}

	private def addNewSlackAssociation(userId: String, userName: String, p: Person): String = {
		s"New slack user [$userId/$userName] associated with DB person ${printPerson(p)}"
	}

	private def moveSlackAssociation(userId: String, userName: String, oldPerson: Person, newPerson: Person): String = {
		s"Existing slack user [$userId/$userName] moved FROM DB person ${printPerson(oldPerson)} TO DB person ${printPerson(newPerson)}"
	}

	private def printPerson(p: Person): String = s"[${p.values.personId.get}: ${p.values.nameFirst.get} ${p.values.nameLast.get} (${p.values.email.get})]"
}

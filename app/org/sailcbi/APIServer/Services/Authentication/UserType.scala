package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.Services.PersistenceBroker

abstract class UserType(val userName: String) {
	// Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
	def getPwHashForUser(rootPB: PersistenceBroker): Option[(Int, String)] = None

	def name: String = this.getClass.getName

	def companion: UserTypeObject[_]
}

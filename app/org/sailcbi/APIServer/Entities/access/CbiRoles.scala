package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.Role
import org.sailcbi.APIServer.Entities.access.CbiPermissions._

object CbiRoles {
	// Access Control, Org Management: 512 - 599
	val ROLE_MANAGE_ACCESS = Role(512, "Manage Access", "Create users with any access, update any user, and update who gets access to what", List(
		PERM_MANAGE_USERS_SCREEN,
		PERM_MANAGE_ACCESS
	))
	val ROLE_MANAGE_SUBORDINATES = Role(513, "Manage Subordinates", "Create/edit users with profiles that are subordinate to the logged in user", List(
		PERM_MANAGE_USERS_SCREEN
	))

	// AP Management: 600 - 799
	val ROLE_MANAGE_DOCK_REPORTS = Role(600, "Manage Dock Reports", List(
		PERM_UPDATE_DOCK_REPORT_RESTRICTED
	))

	// JP Management: 800 - 999
	val ROLE_UPDATE_JP_VARS = Role(800, "Update JP Variables", "Update e.g. list of instructors, list of class locations", List(
		PERM_UPDATE_JP_INSTRUCTORS,
		PERM_UPDATE_JP_CLASS_LOCATIONS
	))

	// UAP Management: 1000 - 1099

	// HS Management: 1100 - 1199
	val ROLE_UPDATE_SCHOOLS_AND_FEES = Role(1100, "Update HS Schools and Fees", List(
		PERM_UPDATE_SCHOOLS
	))

	// AP Dock: 1200 - 1299
	val ROLE_DOCKMASTER = Role(1200, "Dockmaster", List(
		PERM_SIGN_DOCK_REPORT
	))

	// JP Dock: 1300 - 1399

	// FO Admin: 1400 - 1599
	val ROLE_UPDATE_PERSON_VARS = Role(1400, "Update Person variables e.g. Events, Tags", List(
		PERM_UPDATE_EVENTS,
		PERM_UPDATE_TAGS
	))

	// Accounting: 1600 - 1699

	// Development, Marketing, Comms: 1700 - 1899
	val ROLE_DEVELOPMENT_ADMIN = Role(1700, "Manage all things development (donation funds etc)", List(
		PERM_UPDATE_DONATION_FUNDS
	))

	// Reporting: 1900 - 1999

	// Devops: 2000 - 2199

}

/*
# ACCESS #
- manage access control
- users screen
- Edit any user
- edit access profiles
- edit profile:role mappings
	- grant override roles to individual users
	- manage subordinate user
	- users screen


	# AP #
	- CRUD class instances
	- CRUD class types/formats
	- override into classes
	- grant arbitrary vouchers
	- grant restricted ratings
	- rescind ratings
	- cancel classes
	- grant classtype overrides

	# JP #
	- CRUD class instances
	- CRUD class types
	- CRUD staggers
	- override into classes
	- grant restricted ratings
	- rescind ratings
	- see EII data
	- grant price overrides
	- CRUD groups
	- create group placeholders


	# HS #
	- CRUD school list
	- update school fees

	# UAP #


	# AP Dock #

	# JP Dock #

	# FO Admin #

	# Development, Marketing, Comms etc #

	# Cross Program #
*/

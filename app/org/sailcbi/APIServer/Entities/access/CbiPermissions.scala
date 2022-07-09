package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.Permission

object CbiPermissions {
	// Access Control, Org Management: 1024 - 1999
	val PERM_MANAGE_USERS_SCREEN = Permission(1024, "Manage Users Screen", "Grants access to the manage users screen.  Does not inherently grant access to actually manage anyone on that screen.")
	val PERM_MANAGE_ACCESS = Permission(1025, "Manage Access", "Manage who gets access to what")

	// AP Management: 2000 - 2999

	// JP Management: 3000 - 3999
	val PERM_UPDATE_JP_INSTRUCTORS = Permission(3000, "Update JP Instructors")
	val PERM_UPDATE_JP_CLASS_LOCATIONS = Permission(3001, "Update JP Class Locations")

	// UAP Management: 4000 - 4999

	// HS Management: 5000 - 5999
	val PERM_UPDATE_SCHOOLS = Permission(5000, "Update High Schools")

	// AP Dock: 6000 - 6999
	val PERM_SIGN_DOCK_REPORT = Permission(6000, "Sign Dock report")
	val PERM_UPDATE_DOCK_REPORT_RESTRICTED = Permission(6001, "Update restricted Dock Report fields")

	// JP Dock: 7000 - 7999

	// FO Admin: 8000 - 9999
	val PERM_UPDATE_EVENTS = Permission(8000, "Update Events")
	val PERM_UPDATE_TAGS = Permission(8001, "Update Tags")

	// Accounting: 10000 - 10999

	// Development, Marketing, Comms: 11000 - 11999
	val PERM_UPDATE_DONATION_FUNDS = Permission(11000, "Update Donation Funds")


	// Reporting: 12000 - 12999

	// Devops: 13000 - 15999



}

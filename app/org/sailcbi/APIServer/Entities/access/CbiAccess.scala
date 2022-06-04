package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.{Permission, Role, Title}

import scala.reflect.runtime.universe.MethodSymbol

object CbiAccess {
	val NO_ROLES: Set[Role] = Set.empty

	object titles {
		val TITLE_EXEC_DIRECTOR = Title(64, "Executive Director", Set(roles.ROLE_GENERAL_ADMIN))

		val TITLE_AP_DIRECTOR = Title(65, "AP Director", Set(roles.ROLE_GENERAL_ADMIN))
		val TITLE_AP_DOCKMASTER = Title(66, "AP Dockmaster", TITLE_AP_DIRECTOR, NO_ROLES)
		val TITLE_AP_DOCKSTAFF = Title(67, "AP Dockstaff", TITLE_AP_DOCKMASTER, NO_ROLES)

		val TITLE_JP_DIRECTOR = Title(68, "JP Director", Set(roles.ROLE_GENERAL_ADMIN))
		val TITLE_JP_DOCKMASTER = Title(69, "JP Dockmaster", TITLE_JP_DIRECTOR, NO_ROLES)
		val TITLE_JP_INSTRUCTOR = Title(70, "JP Instructor", TITLE_JP_DOCKMASTER, NO_ROLES)

		val TITLE_UAP_DIRECTOR = Title(71, "UAP Director", Set(roles.ROLE_GENERAL_ADMIN))
		val TITLE_UAP_DOCKMASTER = Title(72, "UAP Dockmaster", TITLE_UAP_DIRECTOR, NO_ROLES)
		val TITLE_UAP_INSTRUCTOR = Title(73, "UAP Instructor", TITLE_UAP_DOCKMASTER, NO_ROLES)

		val TITLE_HS_DIRECTOR = Title(74, "HS Director", Set(roles.ROLE_GENERAL_ADMIN))

		val TITLE_FO_MANAGER = Title(75, "Front Office Manager", Set(roles.ROLE_GENERAL_ADMIN))
		val TITLE_FO_SUPERVISOR = Title(76, "Front Office Supervisor", TITLE_FO_MANAGER, NO_ROLES)
		val TITLE_FO_STAFF = Title(77, "Front Office Staff", TITLE_FO_SUPERVISOR, NO_ROLES)

		val TITLE_DEVELOPMENT_DIRECTOR = Title(78, "Development Director", Set(roles.ROLE_GENERAL_ADMIN))

		val TITLE_SHOP_DIRECTOR = Title(79, "Shop Director", Set(roles.ROLE_GENERAL_ADMIN))
		val TITLE_SHOP_STAFF = Title(80, "Shop Staff", TITLE_SHOP_DIRECTOR, NO_ROLES)

		val TITLE_ACCOUNTANT = Title(81, "Accountant", NO_ROLES)

		val TITLE_REPORTING_ONLY = Title(82, "Reporting Guest", NO_ROLES)
	}

	object roles {
		val ROLE_GENERAL_ADMIN = Role(512, "General Admin", "Access to misc management screens/functionality", Set(permissions.PERM_GENERAL_ADMIN))
	}

	object permissions {
		val PERM_GENERAL_ADMIN = Permission(1024, "General Admin", "Access to misc management screens/functionality")
	}

	def main(args: Array[String]): Unit = {
		val rm = scala.reflect.runtime.currentMirror

		val permissions = {
			val accessors = rm.classSymbol(Permission.getClass).toType.members.collect {
				case m: MethodSymbol if m.isGetter && m.isPublic => m
			}
			val instanceMirror = rm.reflect(Permission)
			accessors.map(a => {
				a.asMethod.name + ": " + instanceMirror.reflectMethod(a).apply().asInstanceOf[Permission].id + ","
			})
		} ++ {
			val accessors = rm.classSymbol(this.permissions.getClass).toType.members.collect {
				case m: MethodSymbol if m.isGetter && m.isPublic => m
			}
			val instanceMirror = rm.reflect(this.permissions)
			accessors.map(a => {
				a.asMethod.name + ": " + instanceMirror.reflectMethod(a).apply().asInstanceOf[Permission].id + ","
			})
		}
		print(permissions.mkString("\n"))
	}
}

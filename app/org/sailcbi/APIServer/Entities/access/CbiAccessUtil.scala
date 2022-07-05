package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.{AccessProfile, Permission, Role}

import scala.reflect.runtime.universe.MethodSymbol

object CbiAccessUtil {
	lazy val rm = scala.reflect.runtime.currentMirror

	lazy val allAccessProfiles: List[AccessProfile] = {
		val accessors = rm.classSymbol(CbiAccessProfiles.getClass).toType.members.collect {
			case m: MethodSymbol if m.isGetter && m.isPublic => m
		}
		val instanceMirror = rm.reflect(CbiAccessProfiles)
		accessors.map(instanceMirror.reflectMethod(_).apply().asInstanceOf[AccessProfile]).toList
	}

	lazy val accessProfileMap: Map[Int, AccessProfile] = allAccessProfiles.map(ap => (ap.id, ap)).toMap

	lazy val allRoles: List[Role] = ({
		val accessors = rm.classSymbol(Role.getClass).toType.members.collect {
			case m: MethodSymbol if m.isGetter && m.isPublic => m
		}
		val instanceMirror = rm.reflect(Role)
		accessors.map(instanceMirror.reflectMethod(_).apply().asInstanceOf[Role])
	} ++ {
		val accessors = rm.classSymbol(CbiRoles.getClass).toType.members.collect {
			case m: MethodSymbol if m.isGetter && m.isPublic => m
		}
		val instanceMirror = rm.reflect(CbiRoles)
		accessors.map(instanceMirror.reflectMethod(_).apply().asInstanceOf[Role])
	}).toList

	lazy val roleMap: Map[Int, Role] = allRoles.map(r => (r.id, r)).toMap

	lazy val allPermissionsByReflectionName: List[(String, Permission)] = {
		({
			val accessors = rm.classSymbol(Permission.getClass).toType.members.collect {
				case m: MethodSymbol if m.isGetter && m.isPublic => m
			}
			val instanceMirror = rm.reflect(Permission)
			accessors.map(a => {
				val p = instanceMirror.reflectMethod(a).apply().asInstanceOf[Permission]
				(a.asMethod.name.toString, p)
			})
		} ++ {
			val accessors = rm.classSymbol(CbiPermissions.getClass).toType.members.collect {
				case m: MethodSymbol if m.isGetter && m.isPublic => m
			}
			val instanceMirror = rm.reflect(CbiPermissions)
			accessors.map(a => {
				val p = instanceMirror.reflectMethod(a).apply().asInstanceOf[Permission]
				(a.asMethod.name.toString, p)
			})
		}).toList
	}

	lazy val allPermissions: List[Permission] = {
		allPermissionsByReflectionName.map(_._2)
	}

	lazy val permissionMap: Map[Int, Permission] = allPermissions.map(p => (p.id, p)).toMap

	private def findDupeIds(): Unit = {
		val repeatedAccessProfileIds = allAccessProfiles.groupBy(_.id).filter(_._2.length > 1).keys.toList
		if (repeatedAccessProfileIds.nonEmpty) throw new Exception("Repeated AccessProfile ids: " + repeatedAccessProfileIds.mkString(", "))

		val repeatedRoleIds = allRoles.groupBy(_.id).filter(_._2.length > 1).keys.toList
		if (repeatedRoleIds.nonEmpty) throw new Exception("Repeated Role ids: " + repeatedRoleIds.mkString(", "))

		val repeatedPermissionIds = allPermissions.groupBy(_.id).filter(_._2.length > 1).keys.toList
		if (repeatedPermissionIds.nonEmpty) throw new Exception("Repeated Permission ids: " + repeatedPermissionIds.mkString(", "))
	}

	// print all permissions with ids for use in the frontend
	def main(args: Array[String]): Unit = {
		findDupeIds()
		println("export const PERMISSIONS = { ")
		allPermissionsByReflectionName.foreach(p => {
			println("\t" + p._1 + ": " + p._2.id + ",")
		})
		println("};")
		println("export const ROLES = [ ")
		allRoles.reverse.foreach(p => {
			println(s"\t{ id: ${p.id}, name: '${p.name}', description: '${p.description}' },")
		})
		println("];")
		println("export const ACCESS_PROFILES = [ ")
		allAccessProfiles.reverse.foreach(ap => {
			println(s"\t{ id: ${ap.id}, name: '${ap.name}' },")
		})
		println("];")
	}
}

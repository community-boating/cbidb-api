package org.sailcbi.APIServer.Server

import com.coleji.neptune.Core.Boot.ServerBootLoaderLive
import com.coleji.neptune.Core.{RequestCacheObject, RootRequestCache}
import com.coleji.neptune.Util.PropertiesWrapper
import io.sentry.Sentry
import org.sailcbi.APIServer.UserTypes._
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class CBIBootLoaderLive @Inject()(lifecycle: ApplicationLifecycle) extends ServerBootLoaderLive {
	private val PROPERTY_NAMES = CBIBootLoaderLive.PROPERTY_NAMES

	// 3rd member is a function that returns true if the user type is permitted, false if we need to force-disable it even if conf says enable
	private val definedAuthMechanisms: List[(RequestCacheObject[_], String, PropertiesWrapper => Boolean)] = List(
		(RootRequestCache, PROPERTY_NAMES.ROOT_AUTH_ENABLED, _ => true),
		(BouncerRequestCache, PROPERTY_NAMES.BOUNCER_AUTH_ENABLED, _ => true),
		(ApexRequestCache, PROPERTY_NAMES.APEX_AUTH_ENABLED, _ => true),
		(KioskRequestCache, PROPERTY_NAMES.KIOSK_AUTH_ENABLED, _ => true),
		(SymonRequestCache, PROPERTY_NAMES.SYMON_AUTH_ENABLED, _.getOptionalString("SymonSalt").isDefined),
		(StaffRequestCache, PROPERTY_NAMES.STAFF_AUTH_ENABLED, _ => true),
		(MemberRequestCache, PROPERTY_NAMES.MEMBER_AUTH_ENABLED, _ => true),
		(ProtoPersonRequestCache, PROPERTY_NAMES.PROTOPERSON_AUTH_ENABLED, _ => true)
	)

	val requiredProperties = List(
		PROPERTY_NAMES.MEMBER_AUTH_ENABLED,
		PROPERTY_NAMES.KIOSK_AUTH_ENABLED,
		PROPERTY_NAMES.STAFF_AUTH_ENABLED,
		PROPERTY_NAMES.APEX_AUTH_ENABLED,
		PROPERTY_NAMES.SYMON_AUTH_ENABLED,
		PROPERTY_NAMES.BOUNCER_AUTH_ENABLED,
		PROPERTY_NAMES.ROOT_AUTH_ENABLED,
		PROPERTY_NAMES.PROTOPERSON_AUTH_ENABLED,
		PROPERTY_NAMES.APEX_TOKEN,
		PROPERTY_NAMES.APEX_DEBUG_SIGNET,
		PROPERTY_NAMES.STRIPE_API_KEY,
		PROPERTY_NAMES.PREPARED_QUERIES_ONLY,
		PROPERTY_NAMES.ROUTES_SECURITY_LEVEL,
		PROPERTY_NAMES.SYMON_SALT,
		PROPERTY_NAMES.KIOSK_TOKEN,
		PROPERTY_NAMES.SENTRY_DSN
	)

	val paPostBoot: PropertiesWrapper => Unit = pw => {
		// Initialize sentry
		pw.getOptionalString("sentryDSN").foreach(Sentry.init)
		if (pw.contains("sentryDSN")) {
			println("sentry is defined")
		} else {
			println("sentry is NOT defined")
		}
	}

	this.init(lifecycle, CBIBootLoaderLive.ENTITY_PACKAGE_PATH, definedAuthMechanisms,  requiredProperties, paPostBoot)
}

object CBIBootLoaderLive {
	val ENTITY_PACKAGE_PATH = "org.sailcbi.APIServer.Entities.EntityDefinitions"

	object PROPERTY_NAMES {
		val ROOT_AUTH_ENABLED = "RootAuthEnabled"
		val MEMBER_AUTH_ENABLED = "MemberAuthEnabled"
		val KIOSK_AUTH_ENABLED = "KioskAuthEnabled"
		val STAFF_AUTH_ENABLED = "StaffAuthEnabled"
		val APEX_AUTH_ENABLED = "ApexAuthEnabled"
		val SYMON_AUTH_ENABLED = "SymonAuthEnabled"
		val BOUNCER_AUTH_ENABLED = "BouncerAuthEnabled"
		val PROTOPERSON_AUTH_ENABLED = "ProtoPersonAuthEnabled"
		val APEX_TOKEN = "ApexToken"
		val APEX_DEBUG_SIGNET = "ApexDebugSignet"
		val STRIPE_API_KEY = "StripeAPIKey"
		val PREPARED_QUERIES_ONLY = "PreparedQueriesOnly"
		val ROUTES_SECURITY_LEVEL = "RoutesSecurityLevel"
		val SYMON_SALT = "SymonSalt"
		val KIOSK_TOKEN = "KioskToken"
		val SENTRY_DSN = "sentryDSN"
	}
}
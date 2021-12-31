package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Util.DateUtil.HOME_TIME_ZONE
import com.coleji.neptune.Util.PropertiesWrapper
import com.redis.RedisClientPool
import org.sailcbi.APIServer.Server.CBIBootLoaderLive

import java.math.BigInteger
import java.security.MessageDigest
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

class SymonRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)
extends LockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[SymonRequestCache] = SymonRequestCache
}

object SymonRequestCache extends RequestCacheObject[SymonRequestCache] {
	val uniqueUserName = "SYMON"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): SymonRequestCache =
		new SymonRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): SymonRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)


	private def validateSymonHash(
		customParams: PropertiesWrapper,
		host: String,
		program: String,
		argString: String,
		status: Int,
		mac: String,
		candidateHash: String
	): Boolean = {
		println("here we go")
		val symonSalt = customParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.SYMON_SALT)
		val now: String = ZonedDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").withZone(HOME_TIME_ZONE))
		val input = symonSalt + List(host, program, argString, status.toString, mac, now).mkString("-") + symonSalt
		println(input)
		val md5Bytes = MessageDigest.getInstance("MD5").digest(input.getBytes)
		val expectedHash = String.format("%032X", new BigInteger(1, md5Bytes))
		println("expectedHash: " + expectedHash)
		println("candidateHash: " + candidateHash)
		expectedHash == candidateHash
	}

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] = {
		try {
			println("here we go")
			val host: String = request.postParams("symon-host")
			println("symon host is " + host)
			val program = request.postParams("symon-program")
			val argString = request.postParams("symon-argString")
			val status = request.postParams("symon-status").toInt
			val mac = request.postParams("symon-mac")
			val candidateHash = request.postParams("symon-hash")
			println("All args were present")
			val isValid = validateSymonHash(
				customParams = customParams,
				host = host,
				program = program,
				argString = argString,
				status = status,
				mac = mac,
				candidateHash = candidateHash
			)
			if (isValid) Some(uniqueUserName)
			else None
		} catch {
			case e: Throwable => {
				println(e)
				None
			}
		}
	}
}
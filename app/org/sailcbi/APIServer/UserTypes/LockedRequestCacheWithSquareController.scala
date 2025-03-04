package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core.{DatabaseGateway, LockedRequestCache}
import com.coleji.neptune.Util.PropertiesWrapper
import redis.clients.jedis.JedisPool


abstract class LockedRequestCacheWithSquareController(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends LockedRequestCache(userName, serverParams, dbGateway, redisPool) {

}

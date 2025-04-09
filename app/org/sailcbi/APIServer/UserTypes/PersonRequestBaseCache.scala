package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Util.PropertiesWrapper
import redis.clients.jedis.JedisPool

abstract class PersonRequestBaseCache(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends LockedRequestCacheWithSquareController(userName, serverParams, dbGateway, redisPool) {
  lazy val getAuthedPersonId: Option[Int] = {
     None
  }
}

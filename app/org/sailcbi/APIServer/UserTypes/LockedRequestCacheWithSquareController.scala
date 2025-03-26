package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core.{DatabaseGateway, LockedRequestCache}
import com.coleji.neptune.IO.HTTP.FromWSClient
import com.coleji.neptune.Util.PropertiesWrapper
import org.sailcbi.APIServer.IO.CompassSquareInterface.{CompassInterfaceLiveService, CompassInterfaceMechanism}
import org.sailcbi.APIServer.Server.CBIBootLoaderLive
import play.api.libs.ws.WSClient
import redis.clients.jedis.JedisPool

import scala.concurrent.ExecutionContext


abstract class LockedRequestCacheWithSquareController(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
  extends LockedRequestCache(userName, serverParams, dbGateway, redisPool) {

  def getCompassIOController(ws: WSClient)(implicit exec: ExecutionContext): CompassInterfaceMechanism = {
    new CompassInterfaceLiveService(
      serverParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.COMPASS_API_URL),
      serverParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.COMPASS_API_KEY),
      new FromWSClient(ws)
    )
  }

}

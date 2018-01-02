package Services


case class ServerStateContainer private[Services](
  serverTimeOffsetSeconds: Long
)

object ServerStateContainer {
  def get: ServerStateContainer = ServerBootLoader.ssc
}
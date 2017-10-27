package Services

import javax.inject.Inject

import play.api.inject.ApplicationLifecycle

class MysqlBroker @Inject() (lifecycle: ApplicationLifecycle, cp: MysqlConnectionPoolConstructor)
  extends RelationalBroker(lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor) {
  val MAX_EXPR_IN_LIST: Int = 900
}
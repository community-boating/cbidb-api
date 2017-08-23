package Services

import javax.inject.Inject

import play.api.inject.ApplicationLifecycle

class MysqlBroker @Inject() (lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor)
  extends RelationalBroker(lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor) {

}
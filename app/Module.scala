import com.google.inject.AbstractModule

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {
  override def configure(): Unit = {
    //bind(classOf[ServerStateWrapper]).to(classOf[ServerStateWrapper])
    // Select active database type
    //bind(classOf[PersistenceBroker]).to(classOf[OracleBroker])
    //  bind(classOf[PersistenceBroker]).to(classOf[MysqlBroker])

    // Select server run mode
    //bind(classOf[ServerRunMode]).toInstance(ServerRunMode.ROOT_MODE)

    // Select active cache application
    //bind(classOf[CacheBroker]).to(classOf[RedisBroker])
    //bind(classOf[PermissionsAuthority]).toInstance(new PermissionsAuthority())
  }
}

import javax.inject.{Inject, Provider, Singleton}

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.{Configuration, Environment}
import slick.jdbc.JdbcBackend.Database

/**
  * This module handles the bindings for the API to the Slick implementation.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection#Programmatic-bindings
  */
class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
  }
}

@Singleton
class DatabaseProvider @Inject()(config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("myapp.database", config)
}

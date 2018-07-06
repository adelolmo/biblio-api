package org.ado.biblio

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.ado.biblio.auth.*
import org.ado.biblio.books.BookDao
import org.ado.biblio.books.BookResource
import org.ado.biblio.books.BooksResource
import org.ado.biblio.books.core.Library
import org.ado.biblio.infrastructure.ObjectMapperBundle
import org.ado.biblio.lend.LendDao
import org.ado.biblio.lend.LendResource
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.sessions.SessionResource
import org.ado.biblio.users.UserDao
import org.ado.biblio.users.UserResource
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import java.time.Clock

class Application : io.dropwizard.Application<Configuration>() {

    override fun getName(): String {
        return "biblio-api"
    }

    override fun initialize(bootstrap: Bootstrap<Configuration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(true))

        bootstrap.addBundle(object : MigrationsBundle<Configuration>() {
            override fun getDataSourceFactory(conf: Configuration): PooledDataSourceFactory {
                return conf.dataSourceFactory
            }

            override fun getMigrationsFileName(): String {
                return "migrations.xml"
            }
        })
        bootstrap.addBundle(ObjectMapperBundle())
    }

    override fun run(configuration: Configuration, environment: Environment) {
        val clock = Clock.systemUTC()

        val dbi = DBIFactory().build(environment, configuration.dataSourceFactory, "$name-db")
        val bookDao = dbi.onDemand(BookDao::class.java)
        val userDao = dbi.onDemand(UserDao::class.java)
        val sessionDao = dbi.onDemand(SessionDao::class.java)
        val lendDao = dbi.onDemand(LendDao::class.java)

        val library = Library(bookDao, userDao, lendDao, clock)

        environment.jersey().register(AuthDynamicFeature(
                ApiUserAuthFilter.Builder<ApiUser>()
                        .setAuthenticator(ApiUserAuthenticator(userDao, sessionDao))
                        .setAuthorizer(ApiUserAuthorizer())
                        .setUnauthorizedHandler(ApiUserUnauthorizedHandler())
                        .setRealm("BIBLIO REALM")
                        .buildAuthFilter()))
        environment.jersey().register(RolesAllowedDynamicFeature::class.java)
        environment.jersey().register(AuthValueFactoryProvider.Binder<ApiUser>(ApiUser::class.java))

        environment.jersey().register(UserResource(userDao, clock))
        environment.jersey().register(SessionResource(sessionDao, userDao, clock))
        environment.jersey().register(BookResource(bookDao, clock))
        environment.jersey().register(BooksResource(bookDao))
        environment.jersey().register(LendResource(library, bookDao))
    }
}

class Configuration(@JsonProperty("database") val dataSourceFactory: DataSourceFactory) : io.dropwizard.Configuration()
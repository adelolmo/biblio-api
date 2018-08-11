package org.ado.biblio

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.ado.biblio.auth.UserAuthFilter
import org.ado.biblio.auth.UserAuthenticator
import org.ado.biblio.auth.UserAuthorizer
import org.ado.biblio.auth.UserUnauthorizedHandler
import org.ado.biblio.books.BookDao
import org.ado.biblio.books.BookResource
import org.ado.biblio.books.BooksResource
import org.ado.biblio.books.core.Library
import org.ado.biblio.infrastructure.ObjectMapperBundle
import org.ado.biblio.isbnsearch.DefaultGoogleBooksDao
import org.ado.biblio.isbnsearch.GoogleBooksApi
import org.ado.biblio.isbnsearch.IsbnSearchResource
import org.ado.biblio.lend.LendDao
import org.ado.biblio.lend.LendResource
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.sessions.SessionResource
import org.ado.biblio.shared.BookHasher
import org.ado.biblio.users.DefaultPasswordHasher
import org.ado.biblio.users.User
import org.ado.biblio.users.UserDao
import org.ado.biblio.users.UserResource
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import org.hashids.Hashids
import org.jdbi.v3.postgres.PostgresPlugin
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Clock


class Application : io.dropwizard.Application<Configuration>() {

    override fun getName(): String {
        return "biblio-api"
    }

    override fun initialize(bootstrap: Bootstrap<Configuration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(false))

        bootstrap.addBundle(object : MigrationsBundle<Configuration>() {
            override fun getDataSourceFactory(conf: Configuration): PooledDataSourceFactory {
                return conf.dataSourceFactory
            }
        })
        bootstrap.addBundle(ObjectMapperBundle())
    }

    override fun run(configuration: Configuration, environment: Environment) {
        val clock = Clock.systemUTC()
        val hashids = Hashids("Hash in biblio", 10)
        val hasher = BookHasher(hashids)
        val passwordHasher = DefaultPasswordHasher()

        val jdbi = JdbiFactory().build(environment, configuration.dataSourceFactory, "$name-db")
                .installPlugin(PostgresPlugin())
                .registerRowMapper(BookDao.BookMapper(hasher))
        val userDao = jdbi.onDemand(UserDao::class.java)
        val sessionDao = jdbi.onDemand(SessionDao::class.java)
        val bookDao = jdbi.onDemand(BookDao::class.java)
        val lendDao = jdbi.onDemand(LendDao::class.java)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(JacksonConverterFactory.create(environment.objectMapper))
                .build()
        val googleBooksApi = retrofit.create(GoogleBooksApi::class.java)
        val googleBooksDao = DefaultGoogleBooksDao(googleBooksApi)

        val library = Library(lendDao, bookDao, clock)

        environment.jersey().register(AuthDynamicFeature(
                UserAuthFilter.Builder<User>()
                        .setAuthenticator(UserAuthenticator(userDao, sessionDao, clock))
                        .setAuthorizer(UserAuthorizer())
                        .setUnauthorizedHandler(UserUnauthorizedHandler())
                        .setRealm("BIBLIO REALM")
                        .buildAuthFilter()))
        environment.jersey().register(RolesAllowedDynamicFeature::class.java)
        environment.jersey().register(AuthValueFactoryProvider.Binder<User>(User::class.java))

        environment.jersey().register(UserResource(userDao, clock, passwordHasher))
        environment.jersey().register(SessionResource(sessionDao, userDao, clock, passwordHasher))
        environment.jersey().register(BookResource(bookDao, hasher))
        environment.jersey().register(BooksResource(bookDao, hasher, clock))
        environment.jersey().register(LendResource(library, hasher))
        environment.jersey().register(IsbnSearchResource(googleBooksDao))
    }
}

class Configuration(@JsonProperty("database") val dataSourceFactory: DataSourceFactory) : io.dropwizard.Configuration()
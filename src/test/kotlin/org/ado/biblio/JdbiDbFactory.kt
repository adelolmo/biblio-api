package org.ado.biblio

import com.codahale.metrics.MetricRegistry
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Jackson
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Environment
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.postgresql.ds.PGSimpleDataSource

class JdbiDbFactory {
    companion object {
        fun create(dataSource: PGSimpleDataSource): Jdbi {
            val environment = Environment("test-env", Jackson.newObjectMapper(), null, MetricRegistry(), null)
            val dataSourceFactory = DataSourceFactory()
            dataSourceFactory.driverClass = "org.postgresql.Driver"
            dataSourceFactory.url = dataSource.url
            dataSourceFactory.user = dataSource.user

            return JdbiFactory().build(environment, dataSourceFactory, "test")
                    .installPlugin(PostgresPlugin())
                    .installPlugin(KotlinPlugin())
                    .installPlugin(KotlinSqlObjectPlugin())
        }
    }
}
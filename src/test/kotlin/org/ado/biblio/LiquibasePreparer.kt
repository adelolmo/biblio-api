package org.ado.biblio

import com.opentable.db.postgres.embedded.DatabasePreparer
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.core.PostgresDatabase
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.DatabaseException
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import javax.sql.DataSource

class LiquibasePreparer(private val migrationFile: String) : DatabasePreparer {
    override fun prepare(ds: DataSource) {
        val postgres: Database = PostgresDatabase()
        try {
            val connection = ds.connection
            val jdbcConnection = JdbcConnection(connection)
            postgres.connection = jdbcConnection
            val liquibase = Liquibase(migrationFile, ClassLoaderResourceAccessor(), jdbcConnection)
            liquibase.update("")
        } catch (e: LiquibaseException) {
            throw IllegalStateException("Failed to execute migrations!", e)
        } finally {
            try {
                postgres.close()
            } catch (e: DatabaseException) {
                throw IllegalStateException("Failed to close database!", e)
            }
        }
    }
}
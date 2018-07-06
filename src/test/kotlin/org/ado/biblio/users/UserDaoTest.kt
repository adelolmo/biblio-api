package org.ado.biblio.users

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import io.dropwizard.jdbi.OptionalContainerFactory
import io.dropwizard.jdbi.args.InstantArgumentFactory
import org.ado.biblio.LiquibasePreparer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import java.sql.Timestamp
import java.time.Instant

class UserDaoTest {

    @Rule
    @JvmField
    val preparedDbRule: PreparedDbRule =
            EmbeddedPostgresRules.preparedDatabase(LiquibasePreparer("migrations.xml"))
    private lateinit var handle: Handle
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        val dataSource = preparedDbRule.testDatabase as PGSimpleDataSource
        dataSource.serverName = "127.0.0.1"
        val dbi = DBI(dataSource)
        dbi.registerArgumentFactory(InstantArgumentFactory())
        dbi.registerContainerFactory(OptionalContainerFactory())
        userDao = dbi.onDemand(UserDao::class.java)
        handle = dbi.open()
    }

    @After
    fun tearDown() {
        handle.close()
    }

    @Test
    fun getting() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))

        assertThat(userDao.get("john"))
                .isEqualTo(User(
                        "john",
                        "salted password",
                        "salt",
                        "USER",
                        Instant.ofEpochMilli(0)
                ))
    }

    @Test
    fun exists() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))

        assertThat(userDao.exists("john")).isTrue()
    }

    @Test
    fun validate() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))

        assertThat(userDao.validate("john", "salted password"))
                .isTrue()
    }

    @Test
    fun validateFails() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))

        assertThat(userDao.validate("john", "wrong password"))
                .isFalse()
    }

    @Test
    fun adding() {
        userDao.add(User(
                "john",
                "salted password",
                "salt",
                "USER",
                Instant.ofEpochMilli(0)
        ))

        val result = handle.select("select * from users where username='john'").iterator().next()
        assertThat(result["username"]).isEqualTo("john")
        assertThat(result["password"]).isEqualTo("salted password")
        assertThat(result["salt"]).isEqualTo("salt")
        assertThat(result["role"]).isEqualTo("USER")
        assertThat((result["created_at"] as Timestamp).toInstant()).isEqualTo(Instant.ofEpochMilli(0))
    }
}
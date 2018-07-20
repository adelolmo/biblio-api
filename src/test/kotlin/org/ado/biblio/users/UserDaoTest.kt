package org.ado.biblio.users

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import org.ado.biblio.JdbiDbFactory
import org.ado.biblio.LiquibasePreparer
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Handle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Timestamp
import java.time.Instant
import java.util.*

class UserDaoTest {

    @Rule
    @JvmField
    val preparedDbRule: PreparedDbRule =
            EmbeddedPostgresRules.preparedDatabase(LiquibasePreparer("migrations.xml"))
    private lateinit var handle: Handle
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        val jdbi = JdbiDbFactory.create(preparedDbRule.testDatabase as PGSimpleDataSource)
        userDao = jdbi.onDemand(UserDao::class.java)
        handle = jdbi.open()
    }

    @After
    fun tearDown() {
        handle.close()
    }

    @Test
    fun getting() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        assertThat(userDao.get("john"))
                .isEqualTo(Optional.of(User(
                        "john",
                        "salted password",
                        "salt",
                        "USER",
                        Instant.ofEpochMilli(0)
                )))
    }

    @Test
    fun exists() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        assertThat(userDao.exists("john")).isTrue()
    }

    @Test
    fun validate() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        assertThat(userDao.validate("john", "salted password"))
                .isTrue()
    }

    @Test
    fun validateFails() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

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

        val user = handle.select("select * from users where username='john'").mapTo(User::class.java).findFirst()
        assertThat(user.get().username).isEqualTo("john")
        assertThat(user.get().password).isEqualTo("salted password")
        assertThat(user.get().salt).isEqualTo("salt")
        assertThat(user.get().role).isEqualTo("USER")
        assertThat(user.get().createdAt).isEqualTo(Instant.ofEpochMilli(0))
    }
}
package org.ado.biblio.lend

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

class LendDaoTest {

    @Rule
    @JvmField
    val preparedDbRule: PreparedDbRule =
            EmbeddedPostgresRules.preparedDatabase(LiquibasePreparer("migrations.xml"))
    private lateinit var handle: Handle
    private lateinit var lendDao: LendDao

    @Before
    fun setUp() {
        val dataSource = preparedDbRule.testDatabase as PGSimpleDataSource
        dataSource.serverName = "127.0.0.1"
        val dbi = DBI(dataSource)
        dbi.registerArgumentFactory(InstantArgumentFactory())
        dbi.registerContainerFactory(OptionalContainerFactory())
        lendDao = dbi.onDemand(LendDao::class.java)
        handle = dbi.open()
    }

    @After
    fun tearDown() {
        handle.close()
    }

    @Test
    fun someoneHasIt() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))
        handle.insert(
                """
            insert into books (username,title,author,created_at,isbn,tags,image_url)
            values ('john','The book','Author M.',:created,'1234','good book','http://image')
            """,
                Timestamp.from(Instant.ofEpochMilli(0))
        )
        handle.insert("""
            insert into lends(book_id,person,created_at)
            values (:bookId,'peter',:createdAt)
            """,
                1, Timestamp.from(Instant.ofEpochMilli(0)))

        assertThat(lendDao.someoneHasIt(1)).isTrue()
    }

    @Test
    fun noOneHasIt() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))
        handle.insert(
                """
            insert into books (id,username,title,author,created_at,isbn,tags,image_url)
            values (1,'john','The book','Author M.',:created,'1234','good book','http://image')
            """,
                Timestamp.from(Instant.ofEpochMilli(0))
        )

        assertThat(lendDao.someoneHasIt(1)).isFalse()
    }

    @Test
    fun adding() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))
        handle.insert(
                """
            insert into books (id,username,title,author,created_at,isbn,tags,image_url)
            values (1,'john','The book','Author M.',:created,'1234','good book','http://image')
            """,
                Timestamp.from(Instant.ofEpochMilli(0))
        )

        lendDao.add(Lend.create(1, "peter", Instant.ofEpochMilli(0)))

        val result = handle.select("select * from lends where id=1").iterator().next()
        assertThat(result["book_id"]).isEqualTo(1L)
        assertThat(result["person"]).isEqualTo("peter")
        assertThat((result["created_at"] as Timestamp).toInstant()).isEqualTo(Instant.ofEpochMilli(0))
        assertThat((result["returned_at"])).isNull()
    }

    @Test
    fun updatingReturnedAt() {
        handle.insert("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """,
                Timestamp.from(Instant.ofEpochMilli(0)))
        handle.insert(
                """
            insert into books (username,title,author,created_at,isbn,tags,image_url)
            values ('john','The book','Author M.',:created,'1234','good book','http://image')
            """,
                Timestamp.from(Instant.ofEpochMilli(0))
        )
        handle.insert("""
            insert into lends(book_id,person,created_at)
            values (:bookId,'peter',:createdAt)
            """,
                1, Timestamp.from(Instant.ofEpochMilli(0)))

        lendDao.updateReturnedAt(1, Instant.ofEpochMilli(1000))

        val result = handle.select("select * from lends where id=1").iterator().next()
        assertThat(result["book_id"]).isEqualTo(1L)
        assertThat(result["person"]).isEqualTo("peter")
        assertThat((result["created_at"] as Timestamp).toInstant()).isEqualTo(Instant.ofEpochMilli(0))
        assertThat((result["returned_at"] as Timestamp).toInstant()).isEqualTo(Instant.ofEpochMilli(1000))
    }
}
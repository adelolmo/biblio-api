package org.ado.biblio.lend

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

class LendDaoTest {

    @Rule
    @JvmField
    val preparedDbRule: PreparedDbRule =
            EmbeddedPostgresRules.preparedDatabase(LiquibasePreparer("migrations.xml"))
    private lateinit var handle: Handle
    private lateinit var lendDao: LendDao

    @Before
    fun setUp() {
        val jdbi = JdbiDbFactory.create(preparedDbRule.testDatabase as PGSimpleDataSource)
        lendDao = jdbi.onDemand(LendDao::class.java)
        handle = jdbi.open()
    }

    @After
    fun tearDown() {
        handle.close()
    }

    @Test
    fun someoneHasIt() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate(
                """
            insert into books (username,title,author,created_at,isbn,tags,image_url)
            values ('john','The book','Author M.',:createdAt,'1234','good book','http://image')
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate("""
            insert into lends(book_id,person,created_at)
            values (:bookId,'peter',:createdAt)
            """)
                .bind("bookId", 1)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        assertThat(lendDao.someoneHasIt(1)).isTrue()
    }

    @Test
    fun noOneHasIt() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate(
                """
            insert into books (id,username,title,author,created_at,isbn,tags,image_url)
            values (1,'john','The book','Author M.',:createdAt,'1234','good book','http://image')
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        assertThat(lendDao.someoneHasIt(1)).isFalse()
    }

    @Test
    fun adding() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate(
                """
            insert into books (id,username,title,author,created_at,isbn,tags,image_url)
            values (1,'john','The book','Author M.',:createdAt,'1234','good book','http://image')
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        lendDao.add(Lend.create(1, "peter", Instant.ofEpochMilli(0)))

        val book = handle.createQuery("select * from lends where id=1").mapTo(Lend::class.java).findFirst()
        assertThat(book.get().bookId).isEqualTo(1L)
        assertThat(book.get().person).isEqualTo("peter")
        assertThat(book.get().createdAt).isEqualTo(Instant.ofEpochMilli(0))
        assertThat(book.get().returnedAt).isNull()
    }

    @Test
    fun updatingReturnedAt() {
        handle.createUpdate("""
            insert into users(username,password,salt,role,created_at)
            values ('john','salted password','salt','USER',:createdAt)
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate("""
            insert into books (username,title,author,created_at,isbn,tags,image_url)
            values ('john','The book','Author M.',:createdAt,'1234','good book','http://image')
            """)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()
        handle.createUpdate("""
            insert into lends(book_id,person,created_at)
            values (:bookId,'peter',:createdAt)
            """)
                .bind("bookId", 1)
                .bind("createdAt", Timestamp.from(Instant.ofEpochMilli(0)))
                .execute()

        lendDao.updateReturnedAt(1, Instant.ofEpochMilli(1000))

        val book = handle.select("select * from lends where id=1").mapTo(Lend::class.java).findFirst()
        assertThat(book.get().bookId).isEqualTo(1L)
        assertThat(book.get().person).isEqualTo("peter")
        assertThat(book.get().createdAt).isEqualTo(Instant.ofEpochMilli(0))
        assertThat(book.get().returnedAt).isEqualTo(Instant.ofEpochMilli(1000))
    }
}
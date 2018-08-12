package org.ado.biblio.books

import org.ado.biblio.shared.IdHasher
import org.ado.biblio.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import javax.ws.rs.core.Response

/**
 * @author Andoni del Olmo
 * @since 10.08.18
 */
class BooksResourceTest {

    private val bookDao: BookDao = Mockito.mock(BookDao::class.java)
    private val clock: Clock = Clock
            .fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
    private val idHasher: IdHasher = Mockito.mock(IdHasher::class.java)
    private lateinit var booksResource: BooksResource

    @Before
    fun setup() {
        booksResource = BooksResource(bookDao, idHasher, clock)
    }

    @Test
    fun adding() {
        `when`(idHasher.encode(1)).thenReturn("ID")
        val book = Book("ID", "john", "Kotlin for dummies", "Mark T. Narrow", "1234",
                "good, manual", clock.instant(), "http://something")
        `when`(bookDao.add("john",
                clock.instant(),
                book))
                .thenReturn(1)

        val response =
                booksResource.add(User("john", "pass", "salt", "USER", clock.instant()),
                        book)

        assertThat(response.statusInfo).isEqualTo(Response.Status.CREATED)
        assertThat(response.headers["Location"]!!.first()).isEqualTo(URI.create("books/ID"))
    }
}
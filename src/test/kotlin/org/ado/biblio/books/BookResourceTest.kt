package org.ado.biblio.books

import org.ado.biblio.shared.Hasher
import org.ado.biblio.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response

class BookResourceTest {

    private val bookDao: BookDao = mock(BookDao::class.java)
    private val clock: Clock = Clock
            .fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
    private val hasher: Hasher = mock(Hasher::class.java)
    lateinit var bookResource: BookResource

    @Before
    fun setup() {
        bookResource = BookResource(bookDao, clock, hasher)
    }

    @Test(expected = NotFoundException::class)
    fun gettingWrongHash() {
        `when`(hasher.decode("ID")).thenThrow(NotFoundException())

        bookResource.get(User("john", "pass", "salt", "USER", clock.instant())
                , "ID")
    }

    @Test(expected = NotFoundException::class)
    fun gettingBookNotFound() {
        `when`(hasher.decode("ID")).thenReturn(Optional.of(1))
        `when`(bookDao.get("john", 1)).thenThrow(NotFoundException())

        bookResource.get(User("john", "pass", "salt", "USER", clock.instant())
                , "ID")
    }

    @Test
    fun getting() {
        `when`(hasher.decode("ID")).thenReturn(Optional.of(1))
        val book = Book("ID", "john", "Kotlin for dummies", "Mark T. Narrow", "1234",
                "good, manual", clock.instant(), "http://something")
        `when`(bookDao.get("john", 1)).thenReturn(book)

        assertThat(bookResource.get(User("john", "pass", "salt", "USER", clock.instant())
                , "ID")).isEqualTo(book)
    }

    @Test
    fun adding() {
        val book = Book("ID", "john", "Kotlin for dummies", "Mark T. Narrow", "1234",
                "good, manual", clock.instant(), "http://something")
        `when`(bookDao.add("john",
                clock.instant(),
                book))
                .thenReturn(1)

        val response =
                bookResource.add(User("john", "pass", "salt", "USER", clock.instant()),
                        book)

        assertThat(response.statusInfo).isEqualTo(Response.Status.CREATED)
        assertThat(response.headers["Location"]!!.first()).isEqualTo(URI.create("books/1"))
    }
}
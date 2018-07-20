package org.ado.biblio.isbnsearch

import org.ado.biblio.books.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.ws.rs.NotFoundException

class IsbnSearchResourceTest {

    private val googleBooksDaoDao: GoogleBooksDao = mock(GoogleBooksDao::class.java)
    lateinit var isbnSearchResource: IsbnSearchResource

    @Before
    fun setup() {
        isbnSearchResource = IsbnSearchResource(googleBooksDaoDao)
    }

    @Test(expected = NotFoundException::class)
    fun gettingGoogleError() {
        `when`(googleBooksDaoDao.get("kotlin")).thenReturn(Optional.empty())

        isbnSearchResource.get("kotlin")
    }

    @Test
    fun gettingNoBooks() {
        `when`(googleBooksDaoDao.get("kotlin"))
                .thenReturn(Optional.of(GoogleBooksApi.Volumes("kind", 0, emptyList())))

        assertThat(isbnSearchResource.get("kotlin").books).isEmpty()
    }

    @Test
    fun getting() {
        val volumeInfo =
                GoogleBooksApi.VolumeInfo(
                        "Title",
                        listOf("Mark T."),
                        listOf(
                                GoogleBooksApi.IndustryIdentifier(GoogleBooksApi.IndustryIdentifierTypeEnum.ISBN_10, "10"),
                                GoogleBooksApi.IndustryIdentifier(GoogleBooksApi.IndustryIdentifierTypeEnum.ISBN_13, "13")
                        ),
                        GoogleBooksApi.ImageLinks("small", "normal")
                )
        `when`(googleBooksDaoDao.get("kotlin"))
                .thenReturn(Optional.of(GoogleBooksApi.Volumes("kind", 1,
                        listOf(GoogleBooksApi.Item("books#volume", "id", "etag", "url", volumeInfo)))))

        assertThat(isbnSearchResource.get("kotlin").books)
                .containsOnly(Book(null, null, "Title", "Mark T.", "13", null, null, "normal"))
    }

}
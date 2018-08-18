package org.ado.biblio.isbnsearch

import io.dropwizard.auth.Auth
import org.ado.biblio.books.Book
import org.ado.biblio.books.BooksDto
import org.ado.biblio.users.User
import java.util.stream.Collectors
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/isbnsearch")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class IsbnSearchResource(private val googleBooksDaoDao: GoogleBooksDao) {

    @GET
    fun get(@Auth user: User, @QueryParam("q") isbn: String): BooksDto {
        val volumes =
                googleBooksDaoDao.get(isbn).orElseThrow { NotFoundException("no book found with isbn: $isbn") }

        return BooksDto(volumes.items.stream()
                .map {
                    Book.of(it.volumeInfo.title,
                            it.volumeInfo.authors.stream().collect(Collectors.joining(",")),
                            isbn(it.volumeInfo.industryIdentifiers),
                            it.volumeInfo.imageLinks.thumbnail)
                }.collect(Collectors.toList()))
    }

    private fun isbn(industryIdentifiers: List<GoogleBooksApi.IndustryIdentifier>): String {
        var fallbackIsbn = ""
        for (industryIdentifier in industryIdentifiers) {
            if (GoogleBooksApi.IndustryIdentifierTypeEnum.ISBN_13 == industryIdentifier.type) {
                return industryIdentifier.identifier
            }
            fallbackIsbn = industryIdentifier.identifier
        }
        return fallbackIsbn
    }
}
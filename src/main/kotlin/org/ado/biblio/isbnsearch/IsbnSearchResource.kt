package org.ado.biblio.isbnsearch

import org.ado.biblio.books.Book
import org.ado.biblio.books.BooksDto
import java.util.stream.Collectors
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/isbnsearch")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class IsbnSearchResource(private val googleBooksDaoDao: GoogleBooksDao) {

    @GET
    fun get(@QueryParam("q") q: String): BooksDto {
        val volumes =
                googleBooksDaoDao.get(q).orElseThrow { NotFoundException("no book found for search term $q") }

        return BooksDto(volumes.items.stream()
                .map { i ->
                    Book.of(i.volumeInfo.title,
                            i.volumeInfo.authors.stream().collect(Collectors.joining(",")),
                            isbn(i.volumeInfo.industryIdentifiers),
                            i.volumeInfo.imageLinks.thumbnail)
                }.collect(Collectors.toList()))
    }

    private fun isbn(industryIdentifiers: List<GoogleBooksApi.IndustryIdentifier>): String {
        var isbn = ""
        for (industryIdentifier in industryIdentifiers) {
            isbn = industryIdentifier.identifier
            if (industryIdentifier.type == GoogleBooksApi.IndustryIdentifierTypeEnum.ISBN_13) {
                return isbn
            }
        }
        return isbn
    }
}
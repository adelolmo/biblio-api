package org.ado.biblio.books

import io.dropwizard.auth.Auth
import org.ado.biblio.shared.Hasher
import org.ado.biblio.users.User
import java.time.Clock
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@Path("books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BooksResource(private val bookDao: BookDao,
                    private val hasher: Hasher,
                    private val clock: Clock) {

    @GET
    fun get(@Auth user: User): BooksDto {
        return BooksDto(bookDao.getAll(user.username))
    }

    @POST
    fun add(@Auth user: User, @Valid @NotNull book: Book): Response {
        val id = bookDao.add(user.username, Instant.now(clock), book)
        return Response.created(
                UriBuilder.fromResource(BookResource::class.java)
                        .resolveTemplate("id", hasher.encode(id)).build())
                .build()
    }
}

data class BooksDto(val books: List<Book>)
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
class BookResource(
        private val bookDao: BookDao,
        private val clock: Clock,
        private val hasher: Hasher
) {

    @GET
    @Path("{id}")
    fun get(@Auth user: User, @PathParam("id") id: String): Book {
        val bookId = hasher.decode(id)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
        return bookDao.get(user.username, bookId)
                ?: throw NotFoundException("book with id $id not found")
    }

    @POST
    fun add(@Auth user: User, @Valid @NotNull book: Book): Response {
        val id = bookDao.add(user.username, Instant.now(clock), book)
        return Response.created(
                (UriBuilder.fromResource(BookResource::class.java)
                        .path(hasher.encode(id)).build()))
                .build()
    }
}
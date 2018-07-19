package org.ado.biblio.books

import io.dropwizard.auth.Auth
import io.dropwizard.jersey.params.LongParam
import org.ado.biblio.auth.ApiUser
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
class BookResource(private val bookDao: BookDao, private val clock: Clock) {

    @GET
    @Path("{id}")
    fun get(@Auth user: ApiUser, @PathParam("id") id: LongParam): Book {
        return bookDao.get(user.username, id.get()) ?: throw NotFoundException("book with id ${id.get()} not found")
    }

    @POST
    fun add(@Auth user: ApiUser, @Valid @NotNull book: Book): Response {
        val id = bookDao.add(user.username, Instant.now(clock), book)
        return Response.created(
                (UriBuilder.fromResource(BookResource::class.java)
                        .path(id.toString()).build()))
                .build()
    }
}
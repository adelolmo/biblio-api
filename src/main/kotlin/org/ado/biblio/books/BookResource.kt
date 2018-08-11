package org.ado.biblio.books

import io.dropwizard.auth.Auth
import org.ado.biblio.shared.Hasher
import org.ado.biblio.users.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("books/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BookResource(
        private val bookDao: BookDao,
        private val hasher: Hasher) {

    @GET
    fun get(@Auth user: User, @PathParam("id") id: String): Book {
        val bookId = hasher.decode(id)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
        return bookDao.get(user.username, bookId)
                ?: throw NotFoundException("book with id $id not found")
    }
}
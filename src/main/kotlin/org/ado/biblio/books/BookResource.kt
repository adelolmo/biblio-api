package org.ado.biblio.books

import io.dropwizard.auth.Auth
import org.ado.biblio.shared.IdHasher
import org.ado.biblio.users.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("books/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BookResource(
        private val bookDao: BookDao,
        private val idHasher: IdHasher) {

    @GET
    fun get(@Auth user: User, @PathParam("id") id: String): Book {
        val bookId = idHasher.decode(id)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
        return bookDao.get(user.username, bookId)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
    }
}
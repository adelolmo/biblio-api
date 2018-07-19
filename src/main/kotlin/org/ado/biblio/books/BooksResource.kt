package org.ado.biblio.books

import io.dropwizard.auth.Auth
import org.ado.biblio.users.User
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BooksResource(private val bookDao: BookDao) {

    @GET
    fun get(@Auth user: User): BooksDto {
        return BooksDto(bookDao.getAll(user.username))
    }
}

data class BooksDto(val books: List<Book>)
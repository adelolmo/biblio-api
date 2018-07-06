package org.ado.biblio.lend

import io.dropwizard.auth.Auth
import io.dropwizard.jersey.params.LongParam
import org.ado.biblio.auth.ApiUser
import org.ado.biblio.books.BookDao
import org.ado.biblio.books.core.Library
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("book-lends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class LendResource(private val library: Library, private val bookDao: BookDao) {

    @POST
    @Path("{id}")
    fun add(@Auth user: ApiUser, @PathParam("id") bookId: LongParam, personDto: PersonDto): Response {
        if (!bookDao.exists(user.username, bookId.get())) {
            throw NotFoundException("book not found")
        }
        library.lend(bookId.get(), personDto.name)
        return Response.accepted().build()
    }

}

data class PersonDto(@field:NotNull @field:NotEmpty val name: String)

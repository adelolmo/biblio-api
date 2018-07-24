package org.ado.biblio.lend

import io.dropwizard.auth.Auth
import org.ado.biblio.books.core.Library
import org.ado.biblio.shared.Hasher
import org.ado.biblio.users.User
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("books/{id}/lends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class LendResource(private val library: Library, private val hasher: Hasher) {

    @POST
    fun add(@Auth user: User, @PathParam("id") id: String, personDto: PersonDto): Response {
        val bookId = hasher.decode(id)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
        library.lend(user.username, bookId, personDto.name)
        return Response.accepted().build()
    }

    @DELETE
    fun delete(@Auth user: User, @PathParam("id") id: String): Response {
        val bookId = hasher.decode(id)
                .orElseThrow { throw NotFoundException("book with id $id not found") }
        library.giveBack(user.username, bookId)
        return Response.accepted().build()
    }
}

data class PersonDto(@field:NotNull @field:NotEmpty val name: String)

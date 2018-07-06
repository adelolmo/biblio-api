package org.ado.biblio.sessions

import org.ado.biblio.users.Passwords
import org.ado.biblio.users.UserDao
import org.hibernate.validator.constraints.NotEmpty
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@Path("/sessions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SessionResource(val sessionDao: SessionDao, val userDao: UserDao, val clock: Clock) {

    @GET
    @Path("{id}")
    fun get(@NotNull @PathParam("id") id: String): Response {
        val session = sessionDao.get(id)
        return Response.ok(session).build()
    }

    @POST
    fun create(@Valid @NotNull sessionDto: SessionDto): Response {
        if (!userDao.exists(sessionDto.username)) {
            throw NotFoundException("user not found")
        }
        val user = userDao.get(sessionDto.username) ?: throw NotFoundException("user not found")
        if (!userDao.validate(sessionDto.username, Passwords.encode(sessionDto.password, user.salt))) {
            throw NotFoundException("wrong credentials")
        }

        val id = UUID.randomUUID()
        sessionDao.add(Session(id,
                sessionDto.username,
                clock.instant(),
                clock.instant().plus(30, ChronoUnit.DAYS)))
        return Response.created(UriBuilder.fromResource(SessionResource::class.java)
                .path(id.toString()).build())
                .header(HttpHeaders.AUTHORIZATION, id)
                .build()
    }
}

data class SessionDto(@field:NotNull @field:NotEmpty val username: String,
                      @field:NotNull @field:NotEmpty val password: String)

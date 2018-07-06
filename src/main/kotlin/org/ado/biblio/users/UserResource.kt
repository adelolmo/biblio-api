package org.ado.biblio.users

import org.hibernate.validator.constraints.NotEmpty
import java.time.Clock
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class UserResource(private val userDao: UserDao, private val clock: Clock) {

    @POST
    fun add(@Valid @NotNull user: UserDto): Response {
        if (userDao.exists(user.username)) {
            throw NotFoundException("user already exists")
        }

        val passwordHashed = Passwords.encode(user.password)
        userDao.add(User(
                user.username,
                passwordHashed.encodedPassword,
                passwordHashed.salt,
                "USER",
                Instant.now(clock)
        ))
        return Response.accepted().build()
    }
}

data class UserDto(@field:NotNull @field:NotEmpty val username: String,
                   @field:NotNull @field:NotEmpty val password: String)
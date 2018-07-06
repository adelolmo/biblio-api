package org.ado.biblio.auth

import io.dropwizard.auth.Authenticator
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.users.UserDao
import java.util.*
import javax.ws.rs.NotFoundException

class ApiUserAuthenticator(private val userDao: UserDao,
                           private val sessionDao: SessionDao) : Authenticator<Token, ApiUser> {

    override fun authenticate(credentials: Token): Optional<ApiUser> {
        val session = sessionDao.get(credentials.value) ?: throw NotFoundException("session not found")
        val user = userDao.get(session.username) ?: throw NotFoundException("user not found")
        return Optional.of(ApiUser(user.username, "biblio", arrayListOf("USER")))
    }
}

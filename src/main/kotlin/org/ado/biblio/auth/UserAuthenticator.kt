package org.ado.biblio.auth

import io.dropwizard.auth.Authenticator
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.users.User
import org.ado.biblio.users.UserDao
import java.util.*
import javax.ws.rs.NotFoundException

class UserAuthenticator(private val userDao: UserDao,
                        private val sessionDao: SessionDao) : Authenticator<Token, User> {

    override fun authenticate(credentials: Token): Optional<User> {
        val session = sessionDao.get(credentials.value) ?: throw NotFoundException("session not found")
        return Optional.of(userDao.get(session.username) ?: throw NotFoundException("user not found"))
    }
}

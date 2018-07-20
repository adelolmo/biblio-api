package org.ado.biblio.auth

import io.dropwizard.auth.Authenticator
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.users.User
import org.ado.biblio.users.UserDao
import java.time.Clock
import java.util.*
import javax.ws.rs.NotFoundException

class UserAuthenticator(private val userDao: UserDao,
                        private val sessionDao: SessionDao,
                        private val clock: Clock) : Authenticator<Token, User> {

    override fun authenticate(credentials: Token): Optional<User> {
        val session = sessionDao.get(credentials.value).orElseThrow { NotFoundException("session not found") }
        if (session.expiresAt.isBefore(clock.instant())) {
            throw NotFoundException("session not found")
        }
        return Optional.of(userDao.get(session.username).orElseThrow { NotFoundException("user not found") })
    }
}

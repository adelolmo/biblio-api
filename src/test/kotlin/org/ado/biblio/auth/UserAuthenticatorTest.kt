package org.ado.biblio.auth

import org.ado.biblio.sessions.Session
import org.ado.biblio.sessions.SessionDao
import org.ado.biblio.users.User
import org.ado.biblio.users.UserDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.ws.rs.NotFoundException

class UserAuthenticatorTest {

    private val userDao: UserDao = mock(UserDao::class.java)
    private val sessionDao: SessionDao = mock(SessionDao::class.java)
    private val clock: Clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
    lateinit var userAuthenticator: UserAuthenticator

    @Before
    fun setup() {
        userAuthenticator = UserAuthenticator(userDao, sessionDao, clock)
    }

    @Test(expected = NotFoundException::class)
    fun authenticatingNoSessionFound() {
        `when`(sessionDao.get("token")).thenThrow(NotFoundException())

        userAuthenticator.authenticate(Token("token"))
    }

    @Test(expected = NotFoundException::class)
    fun authenticatingSessionExpired() {
        `when`(sessionDao.get("token"))
                .thenReturn(Optional.of(Session(UUID.randomUUID(), "john", clock.instant(), clock.instant())))

        userAuthenticator.authenticate(Token("token"))
    }

    @Test
    fun authenticating() {
        `when`(sessionDao.get("token"))
                .thenReturn(Optional.of(Session(UUID.randomUUID(), "john", clock.instant(), clock.instant().plusMillis(1))))
        val user = User("user", "hashed", "salt", "USER", Instant.now(clock))
        `when`(userDao.get("john")).thenReturn(Optional.of(user))

        assertThat(userAuthenticator.authenticate(Token("token"))).isEqualTo(Optional.of(user))
    }
}

package org.ado.biblio.users

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import javax.ws.rs.NotFoundException

class UsersResourceTest {

    private val userDao: UserDao = mock(UserDao::class.java)
    private val clock: Clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
    private val passwordHasher: PasswordHasher = mock(PasswordHasher::class.java)
    private lateinit var usersResource: UsersResource

    @Before
    fun setup() {
        usersResource = UsersResource(userDao, clock, passwordHasher)
    }

    @Test(expected = NotFoundException::class)
    fun addingExistingUser() {
        `when`(userDao.exists("user")).thenReturn(true)

        usersResource.add(UserDto("user", "pass"))

        verify(userDao).exists("user")
        verifyNoMoreInteractions(userDao)
    }

    @Test
    fun adding() {
        `when`(userDao.exists("user")).thenReturn(false)
        `when`(passwordHasher.encode("pass"))
                .thenReturn(PasswordHashed("salt", "hashed"))

        usersResource.add(UserDto("user", "pass"))

        verify(userDao).add(User("user", "hashed", "salt", "USER", Instant.now(clock)))
    }
}
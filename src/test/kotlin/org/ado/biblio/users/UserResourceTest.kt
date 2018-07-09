package org.ado.biblio.users

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class UserResourceTest {

    private val userDao: UserDao = mock(UserDao::class.java)
    private val clock: Clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"))
    private val passwordHasher: PasswordHasher = mock(PasswordHasher::class.java)
    private lateinit var userResource: UserResource

    @Before
    fun setup() {
        userResource = UserResource(userDao, clock, passwordHasher)
    }

    @Test
    fun adding() {
        `when`(passwordHasher.encode("pass"))
                .thenReturn(PasswordHashed("salt", "hashed"))

        userResource.add(UserDto("user", "pass"))

        verify(userDao).add(User("user", "hashed", "salt", "USER", Instant.now(clock)))
    }
}
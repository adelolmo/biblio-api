package org.ado.biblio.auth

import io.dropwizard.auth.Authorizer
import org.ado.biblio.users.User

class UserAuthorizer : Authorizer<User> {
    override fun authorize(user: User, role: String): Boolean {
        return user.role == role
    }
}
package org.ado.biblio.auth

import io.dropwizard.auth.Authorizer

class ApiUserAuthorizer : Authorizer<ApiUser> {
    override fun authorize(user: ApiUser, role: String): Boolean {
        return user.roles.contains(role)
    }
}

package org.ado.biblio.auth

import java.security.Principal

class ApiUser(
        val username: String,
        val organization: String,
        val roles: List<String>
) : Principal {

    override fun getName(): String {
        return username
    }
}
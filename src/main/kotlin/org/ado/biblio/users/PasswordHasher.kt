package org.ado.biblio.users

interface PasswordHasher {

    fun encode(password: String): PasswordHashed

    fun encode(password: String, salt: String): String
}

data class PasswordHashed(val salt: String, val encodedPassword: String)
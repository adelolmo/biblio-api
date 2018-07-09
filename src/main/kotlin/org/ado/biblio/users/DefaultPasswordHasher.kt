package org.ado.biblio.users

import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class DefaultPasswordHasher : PasswordHasher {

    override fun encode(password: String): PasswordHashed {
        val saltBytes = ByteArray(32)
        SecureRandom().nextBytes(saltBytes)
        val salt = DigestUtils.sha256Hex(saltBytes)
        return PasswordHashed(salt, DigestUtils.sha256Hex(salt + password))
    }

    override fun encode(password: String, salt: String): String {
        return DigestUtils.sha256Hex(salt + password)
    }
}
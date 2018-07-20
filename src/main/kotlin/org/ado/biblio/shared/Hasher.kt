package org.ado.biblio.shared

import org.hashids.Hashids
import java.util.*


interface Hasher {
    fun encode(number: Long): String
    fun decode(id: String): Optional<Long>
}

class BookHasher(val hashids: Hashids) : Hasher {

    override fun encode(number: Long): String {
        return hashids.encode(number)
    }

    override fun decode(id: String): Optional<Long> {
        val decode = hashids.decode(id)
        if (decode.size == 1) {
            return Optional.of(decode[0])
        }
        return Optional.empty()
    }
}
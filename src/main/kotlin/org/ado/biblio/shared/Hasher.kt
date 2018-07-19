package org.ado.biblio.shared

import org.hashids.Hashids
import java.util.*


/**
 * @author Andoni del Olmo
 * @since 19.07.18
 */
class Hasher(val hashids: Hashids) {

    fun encode(number: Long): String {
        return hashids.encode(number)
    }

    fun decode(id: String): Optional<Long> {
        val decode = hashids.decode(id)
        return if (decode.size == 1) {
            Optional.of(decode[0])
        } else Optional.empty()
    }
}
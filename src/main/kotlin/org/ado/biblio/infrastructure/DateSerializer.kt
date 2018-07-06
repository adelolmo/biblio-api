package org.ado.biblio.infrastructure

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import java.io.IOException
import java.util.*

class DateSerializer(t: Class<Date>) : StdSerializer<Date>(t) {

    private val df = ISO8601DateFormat()

    @Throws(IOException::class)
    override fun serialize(value: Date, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(df.format(value))
    }
}
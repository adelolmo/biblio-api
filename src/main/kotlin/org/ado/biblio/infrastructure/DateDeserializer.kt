package org.ado.biblio.infrastructure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import java.io.IOException
import java.text.ParseException
import java.util.*

class DateDeserializer(t: Class<Date>) : StdDeserializer<Date>(t) {

    private val df = ISO8601DateFormat()


    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Date {
        val text = jp.text
        try {
            return df.parse(text)
        } catch (e: ParseException) {
            throw IllegalArgumentException(String.format("%s must be of format ISO 8601.", jp.currentName))
        }

    }
}
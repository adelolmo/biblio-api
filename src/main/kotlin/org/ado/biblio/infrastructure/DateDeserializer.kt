package org.ado.biblio.infrastructure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import java.io.IOException
import java.text.ParseException
import java.util.*

class DateDeserializer(t: Class<Date>) : StdDeserializer<Date>(t) {

    private val df = StdDateFormat()

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Date {
        val text = jp.text
        try {
            return df.parse(text)
        } catch (e: ParseException) {
            throw IllegalArgumentException("${jp.currentName} must be of format ISO 8601.")
        }
    }
}
package org.ado.biblio.infrastructure

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.dropwizard.Bundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ObjectMapperBundle : Bundle {

    override fun initialize(bootstrap: Bootstrap<*>) {
        // nothing doing
    }

    override fun run(env: Environment) {
        val om = env.objectMapper
        val module = SimpleModule()
        module.addSerializer(Date::class.java, DateSerializer(Date::class.java))
        module.addDeserializer(Date::class.java, DateDeserializer(Date::class.java))
        module.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        om.registerModule(module)
        val jdk8Module = Jdk8Module()
        jdk8Module.configureAbsentsAsNulls(true)
        val javaTimeModule = JavaTimeModule()
        om.registerModule(javaTimeModule)
        om.registerModule(jdk8Module)
        om.registerModule(ParameterNamesModule())
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}
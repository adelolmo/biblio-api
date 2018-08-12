package org.ado.biblio

import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * @author Andoni del Olmo
 * @since 12.08.18
 */
class Vagrant {

    private val logger = LoggerFactory.getLogger(Vagrant::class.java)

    fun start() {
        logger.info("starting vagrant...")
        val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:18091/")
                .build()
        val healthCheckApi = retrofit.create(HealthCheckApi::class.java)
        val healthCheckDao = HealthCheckDao(healthCheckApi)

        val process = Runtime.getRuntime().exec(arrayOf("/usr/bin/vagrant", "up"))
        try {
            val statusCode = process.waitFor()
            val error = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)
            if (statusCode != 0)
                logger.info("process status code {}", statusCode)
            if ("" != error) {
                logger.info("error $error")
                throw IOException("Cannot start vagrant VM.")
            }
            while (!healthCheckDao.isApplicationRunning()) {
                logger.info("waiting for application to start...")
                try {
                    TimeUnit.SECONDS.sleep(1)
                } catch (e: Exception) {
                    Thread.currentThread().interrupt()
                }
            }

        } catch (e: Exception) {
            throw IOException("Cannot start vagrant VM.", e)
        } finally {
            process.destroy()
        }
    }

    fun stop() {
        logger.info("stopping vagrant...")
        val process = Runtime.getRuntime().exec(arrayOf("/usr/bin/vagrant", "halt"))
        try {
            val statusCode = process.waitFor()
            val error = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)
            if (statusCode != 0)
                logger.info("process status code {}", statusCode)
            if ("" != error) {
                logger.info("error $error")
                throw IOException("Cannot stop vagrant VM.")
            }

        } catch (e: Exception) {
            throw IOException("Cannot stop vagrant VM.", e)
        } finally {
            process.destroy()
        }
    }
}
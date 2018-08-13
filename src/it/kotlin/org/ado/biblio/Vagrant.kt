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

    private val logger = LoggerFactory.getLogger("vagrant")

    fun start() {
        up()
        provision()
    }

    fun stop() {
        logger.info("stopping vagrant...")
        val process = Runtime.getRuntime().exec(arrayOf("/usr/bin/vagrant", "suspend"))
        try {
            val statusCode = process.waitFor()
            val error = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)
            if (statusCode != 0)
                logger.info("process status code $statusCode")
            if ("" != error) {
                logger.error("error $error")
                throw IOException("Cannot stop vagrant.")
            }
            logger.info("vagrant stopped.")

        } catch (e: Exception) {
            throw IOException("Cannot stop vagrant.", e)
        } finally {
            process.destroy()
        }
    }

    private fun up() {
        logger.info("starting vagrant...")
        val process = Runtime.getRuntime().exec(arrayOf("/usr/bin/vagrant", "up"))
        try {
            val statusCode = process.waitFor()
            val error = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)
            if (statusCode != 0)
                logger.info("process status code $statusCode")
            if ("" != error) {
                logger.error("error $error")
                throw IOException("Cannot start vagrant.")
            }
            logger.info("vagrant started.")

        } catch (e: Exception) {
            throw IOException("Cannot start vagrant.", e)
        } finally {
            process.destroy()
        }
    }

    private fun provision() {
        logger.info("provisioning vagrant...")
        val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:18091/")
                .build()
        val healthCheckApi = retrofit.create(HealthCheckApi::class.java)
        val healthCheckDao = HealthCheckDao(healthCheckApi)

        val process = Runtime.getRuntime().exec(arrayOf("/usr/bin/vagrant", "provision"))
        try {
            val statusCode = process.waitFor()
            val error = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)
            if (statusCode != 0)
                logger.info("process status code $statusCode")
            if ("" != error) {
                logger.error("error $error")
                throw IOException("Cannot provision vagrant.")
            }
            while (!healthCheckDao.isApplicationRunning()) {
                logger.info("waiting for application to start...")
                try {
                    TimeUnit.SECONDS.sleep(1)
                } catch (e: Exception) {
                    Thread.currentThread().interrupt()
                }
            }
            logger.info("vagrant provisioned.")

        } catch (e: Exception) {
            throw IOException("Cannot start vagrant.", e)
        } finally {
            process.destroy()
        }
    }
}
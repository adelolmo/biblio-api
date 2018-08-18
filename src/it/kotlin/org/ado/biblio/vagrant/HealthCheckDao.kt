package org.ado.biblio.vagrant

/**
 * @author Andoni del Olmo
 * @since 12.08.18
 */
class HealthCheckDao(private val healthCheckApi: HealthCheckApi) {

    fun isApplicationRunning(): Boolean {
        return try {
            healthCheckApi.check().execute().isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
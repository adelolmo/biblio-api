package org.ado.biblio

/**
 * @author Andoni del Olmo
 * @since 12.08.18
 */
class HealthCheckDao(private val healthCheckApi: HealthCheckApi) {

    fun isApplicationRunning(): Boolean {
        return healthCheckApi.check().execute().isSuccessful
    }
}
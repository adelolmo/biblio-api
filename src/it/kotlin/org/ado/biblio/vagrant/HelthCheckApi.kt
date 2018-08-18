package org.ado.biblio.vagrant

import retrofit2.Call
import retrofit2.http.GET

/**
 * @author Andoni del Olmo
 * @since 12.08.18
 */
interface HealthCheckApi {

    @GET("healthcheck")
    fun check(): Call<Void>
}
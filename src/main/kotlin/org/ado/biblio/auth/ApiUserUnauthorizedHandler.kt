package org.ado.biblio.auth

import io.dropwizard.auth.UnauthorizedHandler
import io.dropwizard.jersey.errors.ErrorMessage
import javax.ws.rs.core.Response

class ApiUserUnauthorizedHandler : UnauthorizedHandler {
    override fun buildResponse(prefix: String, realm: String): Response {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorMessage(Response.Status.UNAUTHORIZED.statusCode, "User not authenticated."))
                .build()
    }
}

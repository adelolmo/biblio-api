package org.ado.biblio.auth

import com.google.common.base.Strings.isNullOrEmpty
import io.dropwizard.auth.AuthFilter
import io.dropwizard.auth.AuthenticationException
import java.io.IOException
import java.security.Principal
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.SecurityContext

@Priority(Priorities.AUTHENTICATION)
class ApiUserAuthFilter<P : Principal> : AuthFilter<Token, P>() {

    override fun filter(requestContext: ContainerRequestContext) {
        val tokenValue = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)
        if (isNullOrEmpty(tokenValue)) {
            throw WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm))
        }
        try {
            val principal = authenticator.authenticate(Token(tokenValue))
            principal.ifPresent { p ->
                requestContext.securityContext = object : SecurityContext {
                    override fun getUserPrincipal(): Principal {
                        return p
                    }

                    override fun isUserInRole(role: String): Boolean {
                        return authorizer.authorize(p, role)
                    }

                    override fun isSecure(): Boolean {
                        return requestContext.securityContext.isSecure
                    }

                    override fun getAuthenticationScheme(): String {
                        return "SECURITY REALM"
                    }
                }
            }
        } catch (e: AuthenticationException) {
            throw IOException("Authentication failed!", e)
        }
    }

    class Builder<P : Principal> :
            AuthFilter.AuthFilterBuilder<Token, P, ApiUserAuthFilter<P>>() {

        override fun newInstance(): ApiUserAuthFilter<P> {
            return ApiUserAuthFilter()
        }

    }
}


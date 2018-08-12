package org.ado.biblio.sessions

import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/sessions/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SessionResource(private val sessionDao: SessionDao) {

    @GET
    fun get(@NotNull @PathParam("id") id: String): Response {
        val session = sessionDao.get(id).orElseThrow { NotFoundException("Session $id not found") }
        return Response.ok(session).build()
    }
}

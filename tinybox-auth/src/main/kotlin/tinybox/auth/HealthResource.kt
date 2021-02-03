package tinybox.auth

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/api/health")
@PermitAll
@Produces(APPLICATION_JSON)
class HealthResource {
    @GET
    fun getHealth() = HealthResponse()

    data class HealthResponse(val status: String = "UP")
}

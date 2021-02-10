package tinybox.common.clients

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/api/users")
@RegisterRestClient(configKey = "tinybox-auth-users")
@RegisterClientHeaders(ApiAuthHeadersFactory::class)
interface UsersClient {
    @GET
    @Path("/names")
    fun getUsersNames(): Uni<List<String>>
}

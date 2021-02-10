package tinybox.common.clients

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/api/login")
@RegisterRestClient(configKey = "tinybox-auth-login")
interface LoginClient {
    @POST
    fun login(request: LoginRequest): LoginResult

    data class LoginRequest(val username: String, val password: String)
    data class LoginResult(val bearer: String)
}

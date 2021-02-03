package tinybox.auth

import io.quarkus.security.credential.PasswordCredential
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.smallrye.jwt.build.Jwt
import tinybox.auth.entity.User
import javax.annotation.security.PermitAll
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/api/login")
@PermitAll
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
class LoginResource(
    private val identityProviderManager: IdentityProviderManager
) {
    @POST
    fun doLogin(request: LoginRequest): LoginResult {
        val result = identityProviderManager.authenticateBlocking(
            UsernamePasswordAuthenticationRequest(
                request.username,
                PasswordCredential(request.password.toCharArray())
            )
        )

        val user = User.findByUsername(result.principal.name)!!

        val token = Jwt
            .issuer("tinybox-auth")
            .upn(user.username)
            .groups(user.roles.map { it.role }.toSet())
            .also {
                if (!user.email.isNullOrBlank())
                    it.claim("email", user.email)
            }
            .sign()

        return LoginResult(token)
    }

    data class LoginRequest(val username: String, val password: String)
    data class LoginResult(val bearer: String)
}

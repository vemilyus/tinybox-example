package tinybox.auth.resources

import io.quarkus.security.credential.PasswordCredential
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.smallrye.jwt.build.Jwt
import io.smallrye.mutiny.Uni
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
    fun doLogin(request: LoginRequest): Uni<LoginResult> =
        identityProviderManager.authenticate(
            UsernamePasswordAuthenticationRequest(
                request.username,
                PasswordCredential(request.password.toCharArray())
            )
        ).map { result ->
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

            LoginResult(token)
        }

    data class LoginRequest(val username: String, val password: String)
    data class LoginResult(val bearer: String)
}

package tinybox.auth.resources

import io.quarkus.elytron.security.common.BcryptUtil
import tinybox.auth.entity.Role
import tinybox.auth.entity.User
import tinybox.common.utils.Constants.Roles.ROLE_ADMIN
import tinybox.common.utils.Constants.Roles.ROLE_API
import tinybox.common.utils.generateRandomString
import javax.annotation.security.RolesAllowed
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/credentials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ApiCredentialsResource {
    @POST
    @RolesAllowed(ROLE_ADMIN)
    @Transactional
    fun createApiCredentials(): ApiCredentials {
        val credentials = ApiCredentials(
            generateRandomString(),
            generateRandomString()
        )

        val user = User(
            credentials.clientId,
            BcryptUtil.bcryptHash(credentials.clientSecret)
        )

        user.roles = listOf(Role.findByRole(ROLE_API)!!)

        user.persist()

        return credentials
    }

    data class ApiCredentials(
        val clientId: String,
        val clientSecret: String
    )
}

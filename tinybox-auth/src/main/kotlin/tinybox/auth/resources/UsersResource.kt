package tinybox.auth.resources

import io.quarkus.elytron.security.common.BcryptUtil
import org.jboss.resteasy.annotations.jaxrs.PathParam
import tinybox.auth.entity.Role
import tinybox.auth.entity.User
import tinybox.auth.utils.Constants.EMAIL_LENGTH
import tinybox.auth.utils.Constants.ROLE_ADMIN
import tinybox.auth.utils.Constants.ROLE_USER
import tinybox.auth.utils.Constants.USERNAME_LENGTH
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.transaction.Transactional
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Path("/api/users")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
class UsersResource {
    private val emailRegex = Regex("""^[^.@\s]+(?:\.[^.@\s]+)*@(?:[^.@\s]+\.)+[^.@\s]+$""")

    @GET
    @RolesAllowed(ROLE_ADMIN)
    fun getUsersList(): List<User> =
        User.listAllSortedByUsername()

    @GET
    @Path("/me")
    @RolesAllowed(ROLE_ADMIN, ROLE_USER)
    fun getCurrentUser(@Context securityContext: SecurityContext): User =
        User.findByUsername(securityContext.userPrincipal.name)!!

    @POST
    @PermitAll
    @Transactional
    fun createUser(request: CreateUserRequest): User {
        val trimmedUsername = request.username.trim()
        val trimmedEmail = request.email.trim()

        when {
            !isValidUsername(trimmedUsername) -> "invalid username"
            !isValidEmail(trimmedEmail) -> "invalid email"
            User.findByUsername(trimmedUsername) != null -> "username taken"
            User.findByEmail(trimmedEmail) != null -> "email taken"
            request.password.isEmpty() -> "empty password"
            else -> null
        }?.let { validationMessage ->
            throw BadRequestException(validationMessage)
        }

        val user = User(
            trimmedUsername,
            BcryptUtil.bcryptHash(request.password),
            trimmedEmail
        )

        user.roles = listOf(Role.findByRole(ROLE_USER)!!)
        user.persist()

        return user
    }

    private fun isValidUsername(username: String) =
        username.isNotEmpty() &&
                username.length <= USERNAME_LENGTH &&
                !username.equals("me", ignoreCase = true)

    private fun isValidEmail(email: String) =
        email.length <= EMAIL_LENGTH && email.matches(emailRegex)

    @PUT
    @Path("/me")
    @RolesAllowed(ROLE_ADMIN, ROLE_USER)
    fun updateCurrentUser(
        request: UpdateUserRequest,
        @Context securityContext: SecurityContext
    ): User {
        val trimmedEmail = request.email?.trim()

        when {
            trimmedEmail != null && !isValidEmail(trimmedEmail) -> "invalid email"
            trimmedEmail != null && User.findByEmail(trimmedEmail) != null -> "email taken"
            request.password != null && request.password.isEmpty() -> "invalid password"
            trimmedEmail == null && request.password == null -> "empty request"
            else -> null
        }?.let { validationMessage ->
            throw BadRequestException(validationMessage)
        }

        val user = User.findByUsername(securityContext.userPrincipal.name)
            ?: throw NotFoundException()

        trimmedEmail?.let { user.email = it }
        request.password?.let { user.password = BcryptUtil.bcryptHash(it) }

        user.persist()

        return user
    }

    @DELETE
    @Path("/me")
    @RolesAllowed(ROLE_USER)
    fun deleteCurrentUser(@Context securityContext: SecurityContext): Response {
        val user = User.findByUsername(securityContext.userPrincipal.name)
            ?: throw NotFoundException()

        deleteUser(user)

        return Response.noContent().build()
    }

    @DELETE
    @Path("/{username}")
    @RolesAllowed(ROLE_ADMIN)
    fun deleteUser(
        @PathParam username: String,
        @Context securityContext: SecurityContext
    ): Response {
        val user = User.findByUsername(username)
            ?: throw NotFoundException()

        if (
            securityContext.userPrincipal.name == user.username &&
            securityContext.isUserInRole(ROLE_ADMIN)
        )
            throw BadRequestException("admin cannot delete themselves")

        deleteUser(user)

        return Response.noContent().build()
    }

    private fun deleteUser(user: User) {
        user.delete()

        // TODO: publish deletion via Kafka
    }

    data class CreateUserRequest(
        val username: String,
        val email: String,
        val password: String
    )

    data class UpdateUserRequest(
        val email: String?,
        val password: String?
    )
}

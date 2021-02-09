package tinybox.auth

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.runtime.StartupEvent
import org.slf4j.LoggerFactory
import tinybox.auth.entity.Role
import tinybox.auth.entity.User
import tinybox.common.utils.Constants.Roles.ROLE_ADMIN
import tinybox.common.utils.Constants.Roles.ROLE_API
import tinybox.common.utils.Constants.Roles.ROLE_USER
import tinybox.common.utils.generateRandomString
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.transaction.Transactional

@ApplicationScoped
class AppLifecycleHandlers {
    private val log = LoggerFactory.getLogger(AppLifecycleHandlers::class.java)!!

    @Transactional
    fun onStart(@Observes event: StartupEvent) {
        initRolesAndAdminUser()
    }

    private fun initRolesAndAdminUser() {
        createRoleIfNotExists(ROLE_API)

        val adminRole = createRoleIfNotExists(ROLE_ADMIN)
        val userRole = createRoleIfNotExists(ROLE_USER)

        if (User.findByUsername("admin") == null) {
            val password = generateRandomString()

            val adminUser = User("admin", BcryptUtil.bcryptHash(password))
            adminUser.roles = listOf(adminRole, userRole)

            adminUser.persist()

            log.info("Created admin user with password \"{}\"", password)
        }
    }

    private fun createRoleIfNotExists(role: String): Role {
        val existingRole = Role.findByRole(role)
        return if (existingRole != null)
            existingRole
        else {
            log.info("Creating role: {}", role)

            val newRole = Role(role)
            newRole.persist()

            newRole
        }
    }
}

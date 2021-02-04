package tinybox.auth

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.runtime.StartupEvent
import org.slf4j.LoggerFactory
import tinybox.auth.entity.Role
import tinybox.auth.entity.User
import tinybox.auth.utils.Constants
import tinybox.auth.utils.toHex
import java.security.SecureRandom
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
        val adminRole = createRoleIfNotExists(Constants.ROLE_ADMIN)
        val userRole = createRoleIfNotExists(Constants.ROLE_USER)

        if (User.findByUsername("admin") == null) {
            val random = SecureRandom.getInstanceStrong()
            val passwordBytes = ByteArray(16)
            random.nextBytes(passwordBytes)

            val password = passwordBytes.toHex()

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

package tinybox.auth

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.runtime.Startup
import org.slf4j.LoggerFactory
import tinybox.auth.entity.Role
import tinybox.auth.entity.User
import tinybox.auth.utils.toHex
import java.security.SecureRandom
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@Startup
@ApplicationScoped
class InitRolesAndAdminUser {
    private val log = LoggerFactory.getLogger(InitRolesAndAdminUser::class.java)!!

    @PostConstruct
    @Transactional
    fun startup() {
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

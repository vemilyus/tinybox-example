package tinybox.auth.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.security.jpa.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.Email

@Entity
@Table(name = "tb_user", schema = "auth")
@UserDefinition
class User : PanacheEntity() {
    companion object : PanacheCompanion<User> {
        fun findByUsername(username: String) = find("username", username).firstResult()

        operator fun invoke(username: String, password: String, email: String? = null) =
            User().apply {
                this.username = username
                this.email = email
                this.password = password
            }
    }

    @Column(nullable = false, length = 1024)
    @Username
    lateinit var username: String

    @Column(unique = true, nullable = true, length = 4096)
    @Email
    var email: String? = null

    @Password
    lateinit var password: String

    @ManyToMany(targetEntity = Role::class)
    @Roles
    lateinit var roles: List<Role>
}

@Entity
@Table(name = "tb_role", schema = "auth")
class Role : PanacheEntity() {
    companion object : PanacheCompanion<Role> {
        fun findByRole(role: String) = find("role", role).firstResult()

        operator fun invoke(role: String) = Role().apply { this.role = role }
    }

    @ManyToMany(mappedBy = "roles", targetEntity = User::class)
    lateinit var users: List<User>

    @RolesValue
    lateinit var role: String
}

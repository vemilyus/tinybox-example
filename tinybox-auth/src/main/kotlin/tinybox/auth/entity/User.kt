package tinybox.auth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.panache.common.Sort
import io.quarkus.security.jpa.*
import tinybox.auth.utils.Constants.EMAIL_LENGTH
import tinybox.auth.utils.Constants.USERNAME_LENGTH
import javax.persistence.*

@Entity
@Table(name = "tb_user", schema = "auth")
@UserDefinition
@JsonIgnoreProperties("id")
class User : PanacheEntity() {
    companion object : PanacheCompanion<User> {
        fun deleteUser(user: User) =
            deleteById(
                user.id ?: throw IllegalArgumentException("User not persisted: ${user.username}")
            )

        fun findByEmail(email: String) = find("email", email).firstResult()
        fun findByUsername(username: String) = find("username", username).firstResult()
        fun listAllSortedByUsername() = listAll(Sort.ascending("username"))

        operator fun invoke(username: String, password: String, email: String? = null) =
            User().apply {
                this.username = username
                this.email = email
                this.password = password
            }
    }

    @Column(nullable = false, length = USERNAME_LENGTH)
    @Username
    lateinit var username: String

    @Column(unique = true, nullable = true, length = EMAIL_LENGTH)
    var email: String? = null

    @JsonIgnore
    @Password
    lateinit var password: String

    @ManyToMany(targetEntity = Role::class, cascade = [CascadeType.REMOVE])
    @Roles
    lateinit var roles: List<Role>
}

@Entity
@Table(name = "tb_role", schema = "auth")
@JsonIgnoreProperties("id")
class Role : PanacheEntity() {
    companion object : PanacheCompanion<Role> {
        fun findByRole(role: String) = find("role", role).firstResult()

        operator fun invoke(role: String) = Role().apply { this.role = role }
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", targetEntity = User::class)
    lateinit var users: List<User>

    @RolesValue
    lateinit var role: String
}

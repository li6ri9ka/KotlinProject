package org.example.app.bootstrap

import org.example.app.auth.BCryptPasswordHasher
import org.example.app.config.AdminBootstrapConfig
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdminBootstrapperTest {
    @Test
    fun `ensureAdmin creates admin when config is enabled and user missing`() {
        val repo = InMemoryUserRepository()
        val hasher = BCryptPasswordHasher()
        val bootstrapper = AdminBootstrapper(repo, hasher)

        bootstrapper.ensureAdmin(
            AdminBootstrapConfig(
                email = "admin@example.com",
                password = "admin123"
            )
        )

        val created = repo.findByEmail("admin@example.com")
        assertNotNull(created)
        assertEquals(Role.ADMIN, created.role)
        assertTrue(hasher.matches("admin123", created.passwordHash))
    }

    @Test
    fun `ensureAdmin does not create duplicate user`() {
        val repo = InMemoryUserRepository().apply {
            create(
                User(
                    id = 0,
                    email = "admin@example.com",
                    passwordHash = "hash",
                    role = Role.ADMIN
                )
            )
        }
        val bootstrapper = AdminBootstrapper(repo, BCryptPasswordHasher())

        bootstrapper.ensureAdmin(
            AdminBootstrapConfig(
                email = "admin@example.com",
                password = "admin123"
            )
        )

        assertEquals(1, repo.count())
    }
}

private class InMemoryUserRepository : UserRepository {
    private val users = linkedMapOf<Long, User>()
    private var nextId = 1L

    override fun create(user: User): User {
        val created = user.copy(id = nextId++)
        users[created.id] = created
        return created
    }

    override fun findByEmail(email: String): User? =
        users.values.firstOrNull { it.email.equals(email, ignoreCase = true) }

    override fun findById(id: Long): User? = users[id]

    fun count(): Int = users.size
}

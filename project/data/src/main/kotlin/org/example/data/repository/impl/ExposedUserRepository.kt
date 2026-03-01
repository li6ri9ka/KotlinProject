package org.example.data.repository.impl

import org.example.data.db.tables.UsersTable
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock
import java.time.OffsetDateTime

class ExposedUserRepository(
    private val clock: Clock = Clock.systemUTC()
) : UserRepository {
    override fun create(user: User): User = transaction {
        val inserted = UsersTable.insert {
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role.name
            it[createdAt] = OffsetDateTime.now(clock)
        }

        val id = inserted[UsersTable.id].value
        user.copy(id = id)
    }

    override fun findByEmail(email: String): User? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.email eq email }
            .limit(1)
            .firstOrNull()
            ?.toUser()
    }

    override fun findById(id: Long): User? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq id }
            .limit(1)
            .firstOrNull()
            ?.toUser()
    }

    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash],
        role = Role.valueOf(this[UsersTable.role])
    )
}

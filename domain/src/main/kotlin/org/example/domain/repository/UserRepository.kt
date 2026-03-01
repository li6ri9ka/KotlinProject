package org.example.domain.repository

import org.example.domain.model.User

interface UserRepository {
    fun create(user: User): User
    fun findByEmail(email: String): User?
    fun findById(id: Long): User?
}

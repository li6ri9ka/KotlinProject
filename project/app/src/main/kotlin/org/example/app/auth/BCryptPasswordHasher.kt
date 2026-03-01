package org.example.app.auth

import org.example.domain.service.auth.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher : PasswordHasher {
    override fun hash(rawPassword: String): String = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

    override fun matches(rawPassword: String, hashedPassword: String): Boolean =
        BCrypt.checkpw(rawPassword, hashedPassword)
}

package org.example.data.db.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    @Volatile
    private var initialized = false

    fun init(config: DatabaseConfig) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            val dataSource = hikari(config)
            if (config.runMigrations) {
                migrate(dataSource)
            }

            Database.connect(dataSource)
            initialized = true
        }
    }

    private fun hikari(config: DatabaseConfig): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            driverClassName = config.driverClassName
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            maximumPoolSize = config.maximumPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }

    private fun migrate(dataSource: HikariDataSource) {
        Flyway
            .configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()
    }
}

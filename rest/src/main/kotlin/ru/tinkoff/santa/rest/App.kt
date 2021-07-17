package ru.tinkoff.santa.rest

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import ru.tinkoff.santa.rest.plugin.configureSerialization
import ru.tinkoff.santa.rest.session.sessionComponents
import ru.tinkoff.santa.rest.session.sessionModule
import ru.tinkoff.santa.rest.user.userComponents
import ru.tinkoff.santa.rest.user.userModule

fun main() {
    val config = ConfigFactory.load().extract<AppConfig>()
    migrate(config.dataBaseConfig)
    val engine = embeddedServer(Netty, port = config.httpConfig.port) {
        di {
            coreComponents(config)
            sessionComponents()
            userComponents()
        }
        configureSerialization()
        sessionModule()
        userModule()
    }
    engine.start()
}


fun DI.Builder.coreComponents(config: AppConfig) {
    bind<AppConfig>() with singleton { config }
    bind<Database>() with singleton {
        Database.connect(
            url = config.dataBaseConfig.url,
            user = config.dataBaseConfig.user,
            password = config.dataBaseConfig.password
        )
    }
}

fun migrate(dataBaseConfig: DataBaseConfig) {
    Flyway
        .configure()
        .dataSource(
            dataBaseConfig.url,
            dataBaseConfig.user,
            dataBaseConfig.password
        )
        .load()
        .migrate()
}
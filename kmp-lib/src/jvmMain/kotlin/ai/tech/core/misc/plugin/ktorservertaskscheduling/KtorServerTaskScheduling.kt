package ai.tech.core.misc.plugin.ktorservertaskscheduling

import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.inmo.krontab.builder.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.redis.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.inmo.krontab.builder.*
import io.ktor.server.application.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.redis.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils

public fun Application.configureKtorServerTaskScheduling(
) {
    install(TaskScheduling) {

    }
}

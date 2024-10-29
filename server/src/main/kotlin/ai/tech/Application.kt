package ai.tech

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import ai.tech.plugin.di.configKoin
import java.io.File

public fun main(args: Array<String>) {
    createKeystore()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() {
    configKoin()
}

private fun createKeystore() {
    val keyStoreFile = File("server/src/main/resources/keystore.p12")

    if (keyStoreFile.exists()) return

    val keyStore = buildKeyStore {
        certificate("applicationTLS") {
            password = "AITech"
            daysValid = 365
            keySizeInBits = 4096
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }

    keyStore.saveToFile(keyStoreFile, "AITech")
}
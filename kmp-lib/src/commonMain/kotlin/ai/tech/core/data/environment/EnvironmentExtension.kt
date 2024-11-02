package ai.tech.core.data.environment

public expect fun getEnv(name: String): String?

public expect suspend fun String.toClipboard()

public expect suspend fun fromClipboard(): String?

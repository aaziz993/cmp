package ai.tech.core.data.filesystem

public actual fun getEnv(name: String): String? = System.getenv(name)

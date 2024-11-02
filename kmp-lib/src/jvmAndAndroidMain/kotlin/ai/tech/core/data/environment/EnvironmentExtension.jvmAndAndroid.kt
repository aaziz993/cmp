package ai.tech.core.data.environment

public actual fun getEnv(name: String): String? = System.getenv(name)

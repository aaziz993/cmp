package ai.tech.core.misc.config

public interface ConfigService{

    public suspend fun initialize()

    public suspend fun getConfig(): Map<String, Any?>

    public companion object {

        public const val APPLICATION_CONFIG_NAME: String = "application"
    }
}

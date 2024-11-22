package ai.tech.core.misc.model.config.presentation

public interface PresentationConfig {

    public val route: String?

    public val startDestination: String

    public val signInRedirectDestination: String

    public val signOutRedirectDestination: String

    public val destination: DestinationConfig
}

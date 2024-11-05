package ai.tech.core.misc.model.config.presentation

public interface PresentationConfig {

    public val routeBase: String?

    public val route: String

    public val signInRedirectRoute: String

    public val signOutRedirectRoute: String

    public val destination: DestinationsConfig
}

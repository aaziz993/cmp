package ai.tech.core.presentation.model.config

public interface SharedPresentationConfig {

    public val route: String?

    public val startDestination: String

    public val signInRedirectDestination: String

    public val signOutRedirectDestination: String

    public val destinations: DestinationConfig
}

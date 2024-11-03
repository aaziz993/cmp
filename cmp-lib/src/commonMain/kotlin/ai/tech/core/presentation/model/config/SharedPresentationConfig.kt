package ai.tech.core.presentation.model.config

public interface SharedPresentationConfig {
    public val routeBase: String?

    public val route: String

    public val signInRedirectRoute: String

    public val signOutRedirectRoute: String
}

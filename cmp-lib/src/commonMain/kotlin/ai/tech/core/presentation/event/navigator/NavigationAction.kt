package ai.tech.core.presentation.event.navigator

public sealed interface NavigationAction {

    /**
     * Navigate back
     */
    public data object NavigateBack : NavigationAction

    public sealed class Navigation : NavigationAction {
        public abstract val route: String

        /**
         * Navigate to route
         */
        public data class Navigate(override val route: String) : Navigation()

        /**
         * Navigate back to specific route inclusive or exclusive
         */
        public data class NavigateBackTo(
            override val route: String,
            public val inclusive: Boolean = false,
            public val saveState: Boolean = false
        ) : Navigation()

        /**
         * Navigate to route and remove current view from nav stack
         */
        public data class NavigateAndClearCurrent(override val route: String) : Navigation()

        /**
         * Navigate to route and remove all previous routes making current one as a top
         */
        public data class NavigateAndClearTop(override val route: String) : Navigation()
    }

    public sealed class SafeNavigation<out T> : NavigationAction {
        public abstract val route: T

        /**
         * Navigate to route
         */
        public data class Navigate<out T : Any>(override val route: T) : SafeNavigation<T>()

        /**
         * Navigate back to specific route inclusive or exclusive
         */
        public data class NavigateBackTo<out T : Any>(
            override val route: T,
            public val inclusive: Boolean = false,
            public val saveState: Boolean = false
        ) : SafeNavigation<T>()

        /**
         * Navigate to route and remove current view from nav stack
         */
        public data class NavigateAndClearCurrent<out T : Any>(override val route: T) : SafeNavigation<T>()

        /**
         * Navigate to route and remove all previous routes making current one as a top
         */
        public data class NavigateAndClearTop<out T : Any>(override val route: T) : SafeNavigation<T>()
    }
}

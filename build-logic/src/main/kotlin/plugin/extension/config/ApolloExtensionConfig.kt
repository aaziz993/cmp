package plugin.extension.config

import com.apollographql.apollo3.gradle.api.ApolloExtension
import org.gradle.api.Project

internal fun Project.configureApolloExtension(extension: ApolloExtension): ApolloExtension =
    extension.apply {
    }

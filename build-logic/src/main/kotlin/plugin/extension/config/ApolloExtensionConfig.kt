package plugin.extension.config

import com.apollographql.apollo3.gradle.api.ApolloExtension
import org.gradle.api.Project

internal fun Project.configApolloExtension(extension: ApolloExtension): ApolloExtension =
    extension.apply {
    }

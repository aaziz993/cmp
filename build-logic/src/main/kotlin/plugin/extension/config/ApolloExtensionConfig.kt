package plugin.extension.config

import com.apollographql.apollo3.gradle.api.ApolloExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings

internal fun Project.configApolloExtension(extension: ApolloExtension): ApolloExtension =
    extension.apply {
        settings.config.applyTo("apollo", this)
    }

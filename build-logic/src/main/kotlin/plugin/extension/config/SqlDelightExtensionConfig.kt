package plugin.extension.config

import app.cash.sqldelight.gradle.SqlDelightExtension
import plugin.extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config

internal fun Project.configSqlDelightExtension(extension: SqlDelightExtension): SqlDelightExtension =
    extension.apply {
        settings.config.applyTo("sqldelight", this)
    }

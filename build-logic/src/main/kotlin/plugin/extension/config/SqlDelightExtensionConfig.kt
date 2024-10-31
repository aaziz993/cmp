package plugin.extension.config

import app.cash.sqldelight.gradle.SqlDelightExtension
import org.gradle.api.Project

internal fun Project.configSqlDelightExtension(extension: SqlDelightExtension): SqlDelightExtension =
    extension.apply {
    }

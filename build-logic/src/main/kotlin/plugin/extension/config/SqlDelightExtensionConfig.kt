package plugin.extension.config

import app.cash.sqldelight.gradle.SqlDelightExtension
import org.gradle.api.Project

internal fun Project.configureSqlDelightExtension(extension: SqlDelightExtension): SqlDelightExtension =
    extension.apply {
    }

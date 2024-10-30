package extension.config

import androidx.room.gradle.RoomExtension
import app.cash.sqldelight.gradle.SqlDelightExtension
import extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config

internal fun Project.configRoomExtension(extension: RoomExtension): RoomExtension =
    extension.apply {
        schemaDirectory("$projectDir/schemas")

        settings.config.applyTo("room", this)
    }

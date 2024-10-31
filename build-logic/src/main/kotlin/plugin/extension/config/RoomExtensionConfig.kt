package plugin.extension.config

import androidx.room.gradle.RoomExtension
import org.gradle.api.Project

internal fun Project.configRoomExtension(extension: RoomExtension): RoomExtension =
    extension.apply {
        schemaDirectory("$projectDir/schemas")
    }

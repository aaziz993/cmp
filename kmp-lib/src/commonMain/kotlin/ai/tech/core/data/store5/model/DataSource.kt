package ai.tech.core.data.store5.model

import kotlinx.serialization.Serializable

@Serializable
public data class DataSource(val memory: Boolean, val disk: Boolean, val remote: Boolean) {

    public companion object {

        public val all: DataSource = DataSource(memory = true, disk = true, remote = true)
        public val localOnly: DataSource = DataSource(memory = true, disk = true, remote = false)
        public val remoteOnly: DataSource = DataSource(memory = false, disk = false, remote = true)
    }
}

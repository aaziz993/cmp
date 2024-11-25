package ai.tech.core.data.store5.model

public data class DataSources(val memory: Boolean, val disk: Boolean, val remote: Boolean) {
    public companion object {
        public val all: DataSources = DataSources(memory = true, disk = true, remote = true)
        public val localOnly: DataSources = DataSources(memory = true, disk = true, remote = false)
        public val remoteOnly: DataSources = DataSources(memory = false, disk = false, remote = true)
    }
}

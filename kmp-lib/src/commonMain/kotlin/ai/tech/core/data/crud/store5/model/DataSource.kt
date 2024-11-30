package ai.tech.core.data.crud.store5.model

public data class DataSource(
    val memory: Boolean,
    val disk: Boolean,
    val remote: Boolean
) {
    public companion object {
        public val all: DataSource = DataSource(memory = true, disk = true, remote = true)
        public val localOnly: DataSource = DataSource(memory = true, disk = true, remote = false)
        public val remoteOnly: DataSource = DataSource(memory = false, disk = false, remote = true)
    }
}


public fun DataSource.isRemoteOnly(): Boolean =
    !this.memory && !this.disk && this.remote

public fun DataSource.isLocalOnly(): Boolean =
    this.memory && this.disk && !this.remote

public fun DataSource.isMemoryOnly(): Boolean =
    this.memory && !this.disk && !this.remote

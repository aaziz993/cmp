package plugin.extension.config.spotless.model

public data class Format(
    val name: String,
    val formats: List<String>,
    val licenseHeaderPath: String,
    val delimiter: String,
)

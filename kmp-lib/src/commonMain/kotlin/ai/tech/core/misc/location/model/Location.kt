package ai.tech.core.misc.location.model

public interface Location {
    public val latitude: Double
    public val longitude: Double
    public val altitude: Double
    public val identifier: String?
    public val description: String?
}

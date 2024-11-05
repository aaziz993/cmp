package customs.cms.model

public interface ScanSource {
    public val customsOffice: String
    public val customsCode: String
    public val path: String
    public val dataPath: String?
    public val isFile: Boolean
    public val mapperId: Long
}
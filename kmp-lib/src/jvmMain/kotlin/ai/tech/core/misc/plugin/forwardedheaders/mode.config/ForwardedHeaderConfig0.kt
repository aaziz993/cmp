package ai.tech.core.misc.plugin.forwardedheaders.mode.config

public interface ForwardedHeaderConfig0 {

    public val useFirst: Boolean?
    public val useLast: Boolean?
    public val skipLastProxies: Int?
    public val skipKnownProxies: List<String>?
}

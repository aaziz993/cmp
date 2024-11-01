package ai.tech.core.data.model

public open class AudioFormat(
    public val sampleRateInHz: Number = 16000,
    public val sampleSizeInBits: Int = 8,
    public val channelCount: Int = 1,
)

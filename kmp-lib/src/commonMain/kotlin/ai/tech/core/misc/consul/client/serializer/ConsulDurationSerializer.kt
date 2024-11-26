package ai.tech.core.misc.consul.client.serializer

import kotlin.math.abs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

public object ConsulDurationSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("GoTimeDuration", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        val totalNanoseconds = value.inWholeNanoseconds
        encoder.encodeString(formatDurationGoStyle(totalNanoseconds))
    }

    override fun deserialize(decoder: Decoder): Duration {
        val durationString = decoder.decodeString()
        return parseGoStyleDuration(durationString)
    }

    private fun formatDurationGoStyle(nanoseconds: Long): String {
        val absNano = abs(nanoseconds)
        val sign = if (nanoseconds < 0) "-" else ""

        // Convert to higher units
        val days = absNano / (24 * 60 * 60 * 1_000_000_000L)
        val remainderAfterDays = absNano % (24 * 60 * 60 * 1_000_000_000L)

        val hours = remainderAfterDays / (60 * 60 * 1_000_000_000L)
        val remainderAfterHours = remainderAfterDays % (60 * 60 * 1_000_000_000L)

        val minutes = remainderAfterHours / (60 * 1_000_000_000L)
        val remainderAfterMinutes = remainderAfterHours % (60 * 1_000_000_000L)

        val seconds = remainderAfterMinutes / 1_000_000_000L
        val remainderAfterSeconds = remainderAfterMinutes % 1_000_000_000L

        val milliseconds = remainderAfterSeconds / 1_000_000L
        val microseconds = (remainderAfterSeconds % 1_000_000L) / 1_000L
        val nanoseconds = remainderAfterSeconds % 1_000L

        // Build the output string
        return buildString {
            append(sign)
            if (days > 0) append("${days}d")
            if (hours > 0) append("${hours}h")
            if (minutes > 0) append("${minutes}m")
            if (seconds > 0) append("${seconds}s")
            if (milliseconds > 0) append("${milliseconds}ms")
            if (microseconds > 0) append("${microseconds}us")
            if (nanoseconds > 0) append("${nanoseconds}ns")
            if (isEmpty()) append("0s")
        }
    }

    // Parses a Go-style duration string into a Kotlin Duration
    private fun parseGoStyleDuration(input: String): Duration {
        val regex = Regex("""(-?\d+(\.\d+)?)(ns|us|µs|ms|s|m|h)""")
        val matches = regex.findAll(input)

        var totalDuration = Duration.ZERO

        for (match in matches) {
            val (value, _, unit) = match.destructured
            val durationValue = value.toDouble()

            val duration = when (unit) {
                "ns" -> durationValue.nanoseconds
                "us", "µs" -> durationValue.microseconds
                "ms" -> durationValue.milliseconds
                "s" -> durationValue.seconds
                "m" -> durationValue.minutes
                "h" -> durationValue.hours
                "d" -> durationValue.days
                else -> throw IllegalArgumentException("Unknown duration unit: $unit")
            }

            totalDuration += duration
        }

        if (totalDuration == Duration.ZERO && input.isNotEmpty()) {
            throw IllegalArgumentException("Invalid Go-style duration: $input")
        }

        return totalDuration
    }
}

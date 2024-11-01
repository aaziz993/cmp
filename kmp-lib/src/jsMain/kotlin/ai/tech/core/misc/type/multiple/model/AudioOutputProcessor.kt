package core.type.multiple.model

import js.array.ReadonlyArray
import js.objects.Record
import js.typedarrays.Float32Array
import web.audio.AudioWorkletProcessorImpl
import web.events.EventHandler

public class AudioOutputProcessor : AudioWorkletProcessorImpl {
    private val byteArrays = ArrayList<ByteArray>()

    init {
        port.onmessage =
            EventHandler { event ->
                byteArrays.add(event.data as ByteArray)
            }
    }

    override fun process(
        inputs: ReadonlyArray<ReadonlyArray<Float32Array>>,
        outputs: ReadonlyArray<ReadonlyArray<Float32Array>>,
        parameters: Record<String, Float32Array>,
    ): Boolean {
        val output = outputs[0]
        if (byteArrays.size > 0) {
            val byteArray = byteArrays.removeFirst()
        }
        return true
    }
}

package core.type.multiple.model

import core.type.multiple.merge
import core.type.multiple.toList
import js.array.ReadonlyArray
import js.objects.Record
import js.typedarrays.Float32Array
import kotlinx.coroutines.flow.merge
import web.audio.AudioWorkletProcessorImpl

internal class AudioInputProcessor : AudioWorkletProcessorImpl {
    override fun process(
        inputs: ReadonlyArray<ReadonlyArray<Float32Array>>,
        outputs: ReadonlyArray<ReadonlyArray<Float32Array>>,
        parameters: Record<String, Float32Array>,
    ): Boolean {
        val input = inputs[0]
        if (input[0].length > 0) {
            port.postMessage(merge(input.map { it.iterator() }).toList().toFloatArray())
        }
        return true
    }
}

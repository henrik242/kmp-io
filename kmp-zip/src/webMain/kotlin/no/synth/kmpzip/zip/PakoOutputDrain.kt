package no.synth.kmpzip.zip

import no.synth.kmpzip.internal.Uint8Array

// Tracks how many bytes of pako's flattened `result` have been returned to
// the caller. While the stream is running, output accumulates in the JS-side
// `chunks` array; pako's onEnd() then flattens whatever chunks are left into
// `result` and clears chunks, so from that point we drain from `result`.
//
// The handover is driven by pako's `ended` flag, not by `result` being absent:
// pako 2 left `result` undefined until onEnd, but pako 3 initializes it to an
// empty Uint8Array, which as an end-of-stream signal reads as "finished with
// no output" and truncates every stream.
internal class PakoOutputDrain {
    private var resultRead: Int = 0

    fun draw(
        chunks: JsAny,
        result: Uint8Array?,
        ended: Boolean,
        output: ByteArray, offset: Int, len: Int,
    ): Int = if (ended && result != null) {
        val pulled = drainResult(result, resultRead, output, offset, len)
        resultRead += pulled
        pulled
    } else {
        drainChunks(chunks, output, offset, len)
    }

    fun isStreamEnd(result: Uint8Array?, ended: Boolean): Boolean =
        ended && result != null && resultRead == result.length

    fun reset() {
        resultRead = 0
    }
}

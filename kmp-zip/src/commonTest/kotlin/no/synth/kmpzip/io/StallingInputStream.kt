package no.synth.kmpzip.io

/**
 * Delivers the first [prefix] bytes of [data], then returns 0 forever instead of -1.
 *
 * Models the misbehaving source the [NoProgressException] guards exist for: an
 * InputStream must report -1 at end of input, and a loop that reads 0 and retries
 * makes no progress.
 */
internal class StallingInputStream(
    private val data: ByteArray,
    private val prefix: Int,
) : InputStream() {
    private var pos = 0

    // The single-byte form cannot express a stall: 0 is a valid byte value, not "no
    // data". Returning it would feed a fabricated 0x00 into byte-at-a-time header
    // parsing and mask exactly the bugs this double exists to expose, so report end of
    // input instead and leave stall modelling to the bulk overload.
    override fun read(): Int {
        if (pos >= prefix) return -1
        return data[pos++].toInt() and 0xFF
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (len == 0) return 0
        if (pos >= prefix) return 0
        val n = minOf(len, prefix - pos)
        data.copyInto(b, off, pos, pos + n)
        pos += n
        return n
    }

    override fun close() {}
}

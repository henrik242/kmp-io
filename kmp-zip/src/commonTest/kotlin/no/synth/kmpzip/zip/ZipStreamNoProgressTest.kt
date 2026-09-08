package no.synth.kmpzip.zip

import no.synth.kmpzip.io.InputStream
import no.synth.kmpzip.io.readBytes
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * A source that stops delivering data without ever reporting EOF fails fast instead of
 * spinning. InputStream.read is contractually -1 at EOF, but implementations that return
 * 0 exist, and the inflate loop used to treat that as "try again" forever — a live-lock
 * that burns a core rather than throwing. See the matching guard in GzipInputStream.
 */
class ZipStreamNoProgressTest {

    /** Delivers [prefix] bytes of [data], then returns 0 forever rather than -1. */
    private class StallingInputStream(
        private val data: ByteArray,
        private val prefix: Int,
    ) : InputStream() {
        private var pos = 0

        override fun read(): Int {
            if (pos >= prefix) return 0
            return data[pos++].toInt() and 0xFF
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if (pos >= prefix) return 0
            val n = minOf(len, prefix - pos)
            data.copyInto(b, off, pos, pos + n)
            pos += n
            return n
        }

        override fun close() {}
    }

    @Test
    fun stalledSourceThrowsInsteadOfSpinning() {
        // binaryZip's deflate payload spans offsets 40..301, so stalling at 150 leaves
        // the inflater mid-stream and wanting input it will never get.
        val data = TestData.binaryZip
        val stream = ZipInputStream(StallingInputStream(data, 150))
        try {
            stream.use { zis ->
                while (true) {
                    zis.nextEntry ?: break
                    zis.readBytes()
                }
            }
            fail("expected a no-progress failure, but the read completed")
        } catch (e: Exception) {
            val msg = e.message ?: ""
            assertTrue(
                msg.contains("no progress") || msg.contains("Truncated"),
                "unexpected failure message: $msg",
            )
        }
    }
}

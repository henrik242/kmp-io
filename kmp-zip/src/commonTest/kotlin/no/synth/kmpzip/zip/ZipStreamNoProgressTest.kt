package no.synth.kmpzip.zip

import no.synth.kmpzip.io.NoProgressException
import no.synth.kmpzip.io.StallingInputStream
import no.synth.kmpzip.io.readBytes
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * A source that stops delivering data without ever reporting EOF fails fast instead of
 * spinning. InputStream.read is contractually -1 at EOF, but implementations that return
 * 0 exist, and these loops used to treat that as "try again" forever — a live-lock that
 * burns a core rather than throwing.
 */
class ZipStreamNoProgressTest {

    @Test
    fun stalledSourceThrowsInsteadOfSpinning() {
        // binaryZip's deflate payload spans offsets 40..301, so stalling at 150 leaves
        // the inflater mid-stream and wanting input it will never get.
        val data = TestData.binaryZip
        assertFailsWith<NoProgressException> {
            ZipInputStream(StallingInputStream(data, 150)).use { zis ->
                while (true) {
                    zis.nextEntry ?: break
                    zis.readBytes()
                }
            }
        }
    }

    @Test
    fun readBytesOnStalledSourceThrowsInsteadOfSpinning() {
        val data = TestData.binaryZip
        assertFailsWith<NoProgressException> {
            StallingInputStream(data, 150).readBytes()
        }
    }
}

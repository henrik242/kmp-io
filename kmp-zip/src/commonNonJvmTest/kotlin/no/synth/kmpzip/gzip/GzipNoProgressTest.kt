package no.synth.kmpzip.gzip

import no.synth.kmpzip.io.NoProgressException
import no.synth.kmpzip.io.StallingInputStream
import no.synth.kmpzip.io.readBytes
import no.synth.kmpzip.zip.TestData
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * The pure-Kotlin gzip reader fails fast on a source that stops delivering data without
 * reporting EOF, rather than spinning. Lives outside commonTest because the jvm actual
 * delegates to java.util.zip, which has its own behaviour here.
 */
class GzipNoProgressTest {

    @Test
    fun stalledSourceThrowsInsteadOfSpinning() {
        // cli.gz is 39 bytes: a 10-byte header, a 21-byte deflate payload and an 8-byte
        // trailer. Stalling at 20 leaves the inflater mid-payload wanting more input.
        assertFailsWith<NoProgressException> {
            GzipInputStream(StallingInputStream(TestData.cliGzip, 20)).readBytes()
        }
    }

    @Test
    fun stalledSourceInHeaderThrowsInsteadOfSpinning() {
        assertFailsWith<NoProgressException> {
            GzipInputStream(StallingInputStream(TestData.cliGzip, 1))
        }
    }
}

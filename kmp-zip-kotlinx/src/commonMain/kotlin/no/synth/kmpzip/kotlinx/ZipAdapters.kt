package no.synth.kmpzip.kotlinx

import kotlinx.io.Sink
import kotlinx.io.Source
import no.synth.kmpzip.zip.ZipInputStream
import no.synth.kmpzip.zip.ZipOutputStream

fun ZipInputStream(source: Source): ZipInputStream {
    return ZipInputStream(SourceInputStream(source))
}

fun ZipOutputStream(sink: Sink): ZipOutputStream {
    return ZipOutputStream(SinkOutputStream(sink))
}

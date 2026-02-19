package no.synth.kmpzip.kotlinx

import kotlinx.io.Sink
import no.synth.kmpzip.io.OutputStream

class SinkOutputStream(private val sink: Sink) : OutputStream() {

    override fun write(b: Int) {
        sink.writeByte(b.toByte())
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        sink.write(b, off, off + len)
    }

    override fun flush() {
        sink.flush()
    }

    override fun close() {
        sink.close()
    }
}

fun Sink.asOutputStream(): OutputStream = SinkOutputStream(this)

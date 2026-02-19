package no.synth.kmpzip.io

expect interface Closeable : AutoCloseable {
    override fun close()
}

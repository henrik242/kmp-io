package no.synth.kmpzip.zip

import no.synth.kmpzip.io.InputStream

expect class ZipInputStream(input: InputStream) : InputStream {
    val nextEntry: ZipEntry?
    fun closeEntry()
    fun readBytes(): ByteArray
    override fun read(): Int
    override fun read(b: ByteArray, off: Int, len: Int): Int
    override fun available(): Int
    override fun close()
}

fun ZipInputStream(data: ByteArray): ZipInputStream {
    return ZipInputStream(no.synth.kmpzip.io.ByteArrayInputStream(data))
}

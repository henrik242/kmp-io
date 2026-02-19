package no.synth.kmpzip.zip

import no.synth.kmpzip.io.OutputStream

expect class ZipOutputStream(output: OutputStream) : OutputStream {
    fun putNextEntry(entry: ZipEntry)
    fun closeEntry()
    override fun write(b: Int)
    override fun write(b: ByteArray, off: Int, len: Int)
    fun finish()
    override fun close()
    fun setComment(comment: String?)
    fun setMethod(method: Int)
    fun setLevel(level: Int)
}

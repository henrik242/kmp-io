@file:JsModule("pako")

package no.synth.kmpzip.zip

import no.synth.kmpzip.internal.Uint8Array

// pako externals - `new pako.Deflate({...})` and `new pako.Inflate({...})`.
// PakoOutputDrain.kt explains how `chunks`, `result` and `ended` interact at
// runtime. pako 3 marks `chunks`, `ended` and `strm` internal in its .d.ts;
// they are still plain fields on the instances, but nothing promises they stay,
// so a pako major bump needs the web deflate/inflate tests re-run.
internal external class Deflate(options: JsAny) {
    val err: Int
    val msg: String
    val ended: Boolean
    val chunks: JsAny
    val result: Uint8Array?
    fun push(data: Uint8Array, flushMode: Int): Boolean
}

internal external class Inflate(options: JsAny) {
    val err: Int
    val msg: String
    val ended: Boolean
    val chunks: JsAny
    val result: Uint8Array?
    // Pako's underlying zlib zstream. We read `avail_in` after push() to
    // figure out how much input pako actually consumed — pako stops at
    // Z_STREAM_END even if there are more bytes in the buffer (the ZIP data
    // descriptor that follows the deflate stream), and the public API doesn't
    // surface this remaining count any other way.
    val strm: JsAny
    fun push(data: Uint8Array, flushMode: Int): Boolean
}

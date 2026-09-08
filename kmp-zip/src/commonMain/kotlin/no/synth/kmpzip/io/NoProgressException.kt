package no.synth.kmpzip.io

/**
 * Thrown when a stream or codec stops making progress: a source that reports neither
 * data nor end of input, or a deflater/inflater that neither consumes input nor
 * produces output. Either would otherwise loop forever, burning CPU instead of failing.
 *
 * This means a misbehaving source rather than a corrupt archive. An [InputStream] must
 * return `-1` at end of input, and [SeekableSource] states the same rule for a
 * non-empty request; returning `0` instead violates both.
 */
class NoProgressException(message: String) : Exception(message)

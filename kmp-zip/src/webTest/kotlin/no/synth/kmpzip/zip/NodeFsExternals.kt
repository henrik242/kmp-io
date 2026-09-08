@file:JsModule("node:fs")

package no.synth.kmpzip.zip

import no.synth.kmpzip.internal.Uint8Array

internal external fun writeFileSync(path: String, data: Uint8Array)

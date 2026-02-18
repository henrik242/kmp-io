# kmp-io

Kotlin Multiplatform I/O and ZIP library for JVM and iOS targets.

Provides `ByteArrayInputStream`, `ByteArrayOutputStream`, `ZipInputStream`, and `ZipOutputStream` with a common API across platforms. On JVM, the implementations delegate to `java.io` and `java.util.zip`. On iOS/Native, they are pure Kotlin implementations using `platform.zlib` for DEFLATE compression and decompression.

## Targets

- **JVM** (also consumable from Android projects)
- **iosX64**, **iosArm64**, **iosSimulatorArm64**

## API

### `no.synth.kmpio.io`

- `Closeable` — interface extending `AutoCloseable`, works with stdlib `use {}`
- `InputStream` — abstract class with `read()`, `read(ByteArray)`, `read(ByteArray, off, len)`, `readBytes()`, `available()`, `skip(Long)`, `mark(Int)`, `reset()`, `markSupported()`, `close()`
- `OutputStream` — abstract class with `write(Int)`, `write(ByteArray)`, `write(ByteArray, off, len)`, `flush()`, `close()`
- `ByteArrayInputStream(ByteArray)` / `ByteArrayInputStream(ByteArray, offset, length)` — full Java-compatible API
- `ByteArrayOutputStream()` / `ByteArrayOutputStream(size)` — auto-growing buffer with `toByteArray()`, `size()`, `reset()`, `writeTo(OutputStream)`

### `no.synth.kmpio.zip`

- `ZipEntry` — properties: `name`, `size`, `compressedSize`, `crc`, `method`, `isDirectory`, `time`, `comment`, `extra`
- `ZipConstants` — `STORED = 0`, `DEFLATED = 8`
- `ZipInputStream(InputStream)` — `nextEntry`, `closeEntry()`, `read()`, `read(ByteArray, off, len)`, `available()`, `close()`
- `ZipInputStream(ByteArray)` — convenience function
- `ZipOutputStream(OutputStream)` — `putNextEntry(ZipEntry)`, `closeEntry()`, `write()`, `finish()`, `close()`, `setMethod(Int)`, `setLevel(Int)`, `setComment(String?)`

## Installation

Published on [Maven Central](https://central.sonatype.com/artifact/no.synth/kmp-io). No special repository configuration needed.

```kotlin
// build.gradle.kts
dependencies {
    implementation("no.synth:kmp-io:0.6.2")
}
```

For KMP projects:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("no.synth:kmp-io:0.6.2")
            }
        }
    }
}
```

## Usage

```kotlin
import no.synth.kmpio.io.ByteArrayInputStream
import no.synth.kmpio.io.ByteArrayOutputStream
import no.synth.kmpio.io.readBytes
import no.synth.kmpio.zip.ZipInputStream
import no.synth.kmpio.zip.ZipOutputStream
import no.synth.kmpio.zip.ZipEntry
import no.synth.kmpio.zip.ZipConstants

// Read from a ByteArray
val stream = ByteArrayInputStream(byteArrayOf(1, 2, 3))
val b = stream.read() // 1

// Read a ZIP from a ByteArray
ZipInputStream(zipBytes).use { zis ->
    while (true) {
        val entry = zis.nextEntry ?: break
        println("${entry.name} (${if (entry.method == ZipConstants.DEFLATED) "deflated" else "stored"})")
        println(zis.readBytes().decodeToString())
    }
}

// Create a ZIP into a ByteArray
val buf = ByteArrayOutputStream()
ZipOutputStream(buf).use { zos ->
    zos.putNextEntry(ZipEntry("hello.txt"))
    zos.write("Hello, world!".encodeToByteArray())
    zos.closeEntry()
}
val zipBytes = buf.toByteArray()
```

## Building

Requires JDK 21 and Xcode (for iOS targets).

```sh
./gradlew build                      # Full build
./gradlew jvmTest                    # JVM tests
./gradlew iosSimulatorArm64Test      # iOS simulator tests
```

## Publishing

Tagging a release triggers the GitHub Actions workflow to publish to Maven Central:

```sh
git tag v0.6.2
git push origin v0.6.2
```

## License

[Mozilla Public License 2.0 (MPL-2.0)](https://opensource.org/license/mpl-2-0)

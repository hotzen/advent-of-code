import kotlinx.coroutines.*
import java.io.File

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun Any?.println() = println(this)

@Deprecated("use input()")
fun getResourceAsFile(path: String): File =
    object {}
        .javaClass
        .classLoader
        .getResource(path)!!
        .path
        .let { File(it) }

fun input(name: String): File = Path("src/main/resources/$name.txt").toFile()

// https://jivimberg.io/blog/2018/05/04/parallel-map-in-kotlin/
suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

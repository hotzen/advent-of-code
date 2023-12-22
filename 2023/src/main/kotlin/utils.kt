package util

import kotlinx.coroutines.*
import java.io.File

fun getResourceAsFile(path: String): File =
    object {}
        .javaClass
        .classLoader
        .getResource(path)!!
        .path
        .let { File(it) }


// https://jivimberg.io/blog/2018/05/04/parallel-map-in-kotlin/
suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

package util

import java.io.File

fun getResourceAsFile(path: String): File =
    object {}
        .javaClass
        .classLoader
        .getResource(path)!!
        .path
        .let { File(it) }

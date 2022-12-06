package day06

import util.getResourceAsFile

fun main(args: Array<String>) {
    getResourceAsFile("day06.txt").forEachLine { line ->
        println(determineIndex(line, 4))
        println(determineIndex(line, 14))
    }
}

fun determineIndex(stream: String, distinctLength: Int): Int {
    val buf = RingBuffer(distinctLength)
    stream.forEachIndexed { idx, c ->
        buf.add(c)
        if (buf.isDistinct()) return idx + 1
    }
    return -1
}

class RingBuffer(val size: Int) {
    private val buffer = arrayOfNulls<Char>(size)
    private var position: Int = -1

    fun add(e: Char) {
        position = (position + 1).mod(size)
        buffer[position] = e
    }

    fun isDistinct() = buffer.none { it == null } && buffer.distinct().size == size
}
package day20

import util.getResourceAsFile

fun main() {
//    val decryptionKey = 1
    val decryptionKey = 811589153

    val moves = getResourceAsFile("day20.txt").useLines { lines ->
        lines.map { Move(it.toLong() * decryptionKey) }.toList()
    }

    // connect pairs
    moves.zipWithNext { a, b ->
        a.next = b
        b.prev = a
    }

    // connect first <-> last
    moves.last().next = moves.first()
    moves.first().prev = moves.last()

//    repeat(1) {
    repeat(10) {
        doMoves(moves)
    }

    val zero = moves.find { it.n == 0L }!!

    var result = 0L
    var node = zero
    repeat(3) {
        repeat(1000) {
            node = node.next!!
        }
        println(node.n)
        result += node.n
    }

    println("RESULT: $result")
}

data class Move(val n: Long) {
    var prev: Move? = null
    var next: Move? = null
}

fun doMoves(moves: List<Move>) {
    for (move in moves) {
        requireNotNull(move.prev)
        requireNotNull(move.next)

        // unlink
        move.prev!!.next = move.next!!
        move.next!!.prev = move.prev!!

        var newPrev = move.prev!!
        var newNext = move.next!!

//        if (move.n > 0) {
//            repeat(move.n) {
//                newPrev = newPrev.next!!
//                newNext = newNext.next!!
//            }
//        } else {
//            repeat(move.n.absoluteValue) {
//                newPrev = newPrev.prev!!
//                newNext = newNext.prev!!
//            }
//        }

        // - dont move multiple times over the whole nodes
        // - make going-backwards into going-forward
        val m = move.n.mod(moves.size - 1)
        repeat(m) {
            newPrev = newPrev.next!!
            newNext = newNext.next!!
        }

        requireNotNull(newPrev)
        requireNotNull(newNext)

        newPrev!!.next = move
        move.prev = newPrev!!

        newNext!!.prev = move
        move.next = newNext!!
    }
}

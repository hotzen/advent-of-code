package day09

import util.getResourceAsFile
import kotlin.math.absoluteValue

fun main() {
    getResourceAsFile("day09.txt").useLines { lines ->
        val headMotions = lines.flatMap { line ->
            val parts = line.split(" ")
            val steps = 1..parts[1].toInt()
            val dir = Direction.from(parts[0])
            steps.map { dir }
        }

//        part1(
//            startPos = Pos(0, 0),
//            headMotions
//        )

        part2(
            startPos = Pos(0, 0),
            headMotions
        )
    }
}

fun part1(startPos: Pos, headMotions: Sequence<Direction>) {
    var headPos = startPos
    var tailPos = startPos
    val allTailPos = mutableSetOf(tailPos)

    for (headMotion in headMotions) {
        val newHeadPos = headMotion.apply(headPos)
        if (!tailPos.isTouching(newHeadPos)) {
            val newTailPos = tailPos + tailPos.delta(newHeadPos).clampToSignedOne()
            allTailPos.add(newTailPos)
            tailPos = newTailPos
        }
        headPos = newHeadPos
    }

    println(
        allTailPos.size
    )
}

fun part2(startPos: Pos, headMotions: Sequence<Direction>) {
    var startRope = (1..10).map { startPos }
    val allTailPos = mutableSetOf(startPos)

    for (headMotion in headMotions) {
        startRope = determineNewRope(headMotion, startRope)
        allTailPos.add(startRope.last())
    }

    println(
        allTailPos.size
    )
}

fun determineNewRope(headMotion: Direction, rope: List<Pos>): List<Pos> {
    val headPos = rope.first()
    val tailRope = rope.drop(1) // no head/tail available in kotlin, that's expensive copying shit

    val newHeadPos = headMotion.apply(headPos)

    // fold the old rope into a new rope by moving currentKnot towards previousKnot
    return tailRope.fold(listOf(newHeadPos)) { newRope, pos ->
        val previousKnot = newRope.last()
        if (previousKnot.isTouching(pos))
            newRope + pos
        else {
            val newPos = pos + pos.delta(previousKnot).clampToSignedOne()
            newRope + newPos
        }
    }
}

enum class Direction(val moveDelta: Pos) {
    DOWN(Pos(0, 1)),
    UP(Pos(0, -1)),
    LEFT(Pos(-1, 0)),
    RIGHT(Pos(1, 0));

    fun apply(pos: Pos): Pos = pos + moveDelta

    companion object {
        fun from(s: String): Direction = when (s) {
            "U" -> UP
            "D" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            else -> throw IllegalArgumentException("invalid direction $s")
        }
    }
}

data class Pos(val col: Int, val row: Int) {
    operator fun plus(move: Pos): Pos = Pos(
        this.col + move.col,
        this.row + move.row
    )

    fun delta(other: Pos): Pos = Pos(
        other.col - this.col,
        other.row - this.row
    )

    fun clampToSignedOne(): Pos = Pos(
        when {
            col >= 1 -> 1
            col <= -1 -> -1
            else -> 0
        },
        when {
            row >= 1 -> 1
            row <= -1 -> -1
            else -> 0
        }
    )

    fun isTouching(other: Pos): Boolean =
        (this.row - other.row).absoluteValue <= 1
                && (this.col - other.col).absoluteValue <= 1

    override fun toString(): String = "($col / $row)"
}

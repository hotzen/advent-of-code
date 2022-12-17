package day17

import util.getResourceAsFile
import java.lang.Integer.max

fun main() {
    val dirs = getResourceAsFile("day17.txt").useLines { lines ->
        lines.flatMap { line ->
            line.toCharArray().map { c ->
                JetDir.values().find { dir -> dir.c == c }!!
            }
        }.toList()
    }

    val chamber = Chamber(7) // The tall, vertical chamber is exactly seven units wide.
    part1(chamber, dirs)
}

fun part1(chamber: Chamber, dirs: List<JetDir>) {
    val rocks = Rock.values()
    var nextDirIdx = 0
    val numberOfRocks = 2022
    for (rockNum in 1..numberOfRocks) {
        val rock = rocks[(rockNum - 1).mod(rocks.size)]
        nextDirIdx = chamber.drop(rock, dirs, nextDirIdx, rockNum)
    }
    println(chamber.highestPoint())
}

data class Pos(val x: Int, val y: Int)

data class Chamber(val width: Int) {
    val xRange = 0..width - 1
    val rubble = Array(width) { mutableSetOf(0) }

    fun highestPoint(): Int = rubble.maxOf { it.maxOrNull() ?: 0 }
    fun highPointsMin(): Int = rubble.minOf { it.maxOrNull() ?: 0 }

    fun drop(rock: Rock, dirs: List<JetDir>, nextDirIdx: Int, rockNum: Int): Int {
        var r = FallingRock(
            rock,
            Pos(
                2,
                highestPoint() + 4
            )
        )

        var dirIdx = nextDirIdx
        while (true) {
            val pushed = push(r, dirs[dirIdx], rockNum)
            dirIdx = (dirIdx + 1).mod(dirs.size)

            val (fell, cameToRest) = fall(pushed, rockNum)
            if (cameToRest) {
                updateRubble(fell.rock.tiles(fell.anchor))
                return dirIdx
            } else {
                r = fell
            }
        }
    }

    fun push(r: FallingRock, dir: JetDir, rockNum: Int): FallingRock {
        val pushedAnchor = dir.apply(r.anchor)
        val pushedTiles = r.rock.tiles(pushedAnchor)
        val crashed = pushedTiles.any { crashedAnything(it) }

        return if (crashed)
            r
        else
            FallingRock(r.rock, pushedAnchor)
    }

    private fun crashedAnything(pos: Pos): Boolean {
        if (!xRange.contains(pos.x)) {
            return true
        }
        return rubble[pos.x].contains(pos.y)
    }

    fun fall(rockBeforeFalling: FallingRock, rockNum: Int): Pair<FallingRock, Boolean> {
        val rock = rockBeforeFalling.rock
        val rockAnchorAfterFalling = Pos(rockBeforeFalling.anchor.x, rockBeforeFalling.anchor.y - 1)

        val fellTiles = rock.tiles(rockAnchorAfterFalling)
        val crashedFloor = fellTiles.any { crashedAnything(it) }
        val final =
            if (crashedFloor)
                rockBeforeFalling
            else
                FallingRock(rock, rockAnchorAfterFalling)

        return Pair(final, crashedFloor)
    }

    fun updateRubble(tiles: List<Pos>) {
        for (tile in tiles) {
            rubble[tile.x].add(tile.y)
        }
    }

    fun toStringWithRock(r: FallingRock?): String {
        val sb = StringBuffer()

        val rockTiles =
            if (r != null)
                r.rock.tiles(r.anchor)
            else
                listOf()

        val highestY = max(
            highestPoint(),
            rockTiles.maxOfOrNull { it.y } ?: 0
        )

        for (y in highestY downTo highPointsMin()) {
            sb.append("|")
            for (x in xRange) {
                val p = Pos(x, y)
                if (rubble[x].contains(y))
                    sb.append("#")
                else if (rockTiles.any { it == p })
                    sb.append("@")
                else
                    sb.append(".")
            }
            sb.append("|\n")
        }

        return sb.toString()
    }

    override fun toString(): String = toStringWithRock(null)
}

data class FallingRock(
    val rock: Rock,
    val anchor: Pos // left bottom
)

enum class JetDir(val c: Char, val move: Int) {
    LEFT('<', -1),
    RIGHT('>', +1);

    fun apply(p: Pos): Pos =
        Pos(
            p.x + move,
            p.y
        )
}

enum class Rock(val relativeTiles: List<Pos>) {
    DASH(
        listOf(
            Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0)
        )
    ),
    PLUS(
        listOf(
            Pos(1, 2),
            Pos(0, 1), Pos(1, 1), Pos(2, 1),
            Pos(1, 0),
        )
    ),
    MIRRL(
        listOf(
            Pos(2, 2),
            Pos(2, 1),
            Pos(0, 0), Pos(1, 0), Pos(2, 0),
        )
    ),
    BIGI(
        listOf(
            Pos(0, 3),
            Pos(0, 2),
            Pos(0, 1),
            Pos(0, 0),
        )
    ),
    SQ(
        listOf(
            Pos(0, 1), Pos(1, 1),
            Pos(0, 0), Pos(1, 0),
        )
    );

    fun tiles(leftBottomAnchorPoint: Pos): List<Pos> =
        relativeTiles.map { relTile ->
            Pos(
                leftBottomAnchorPoint.x + relTile.x,
                leftBottomAnchorPoint.y + relTile.y
            )
        }

    override fun toString(): String = "[${this.name}]"
}
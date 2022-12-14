package day14

import util.getResourceAsFile

fun main() {
    val shapes = getResourceAsFile("day14.txt").useLines { lines ->
        lines.map { line ->
            Shape(
                line.split(" -> ")
                    .map { Pos.from(it) }
                    .windowed(2)
                    .map { w -> Line.from(w[0], w[1]) }
            )
        }.toList()
    }

    val c = Cave(shapes)
    val count = c.countFillSand(POUR_IN)
    println(c.draw())
    println(count)
}

val POUR_IN = Pos(500, 0)

class Cave(val shapes: List<Shape>) {
    val tiles = mutableMapOf<Pos, Tile>()
    val bounds = shapes.map { it.bounds() }.reduce(Bounds::extend)

    init {
        for (shape in shapes) {
            for (line in shape.lines) {
                for (pos in line) {
                    putTile(pos, Tile.ROCK)
                }
            }
        }
    }

    fun tileAt(pos: Pos): Tile = tiles[pos] ?: Tile.AIR

    fun putTile(pos: Pos, tile: Tile) {
        if (tile == Tile.AIR) {
            tiles.remove(pos)
        } else {
            tiles.put(pos, tile)
        }
    }


    fun countFillSand(pourIn: Pos): Int {
        var count = 0
        while (fillOneSand(pourIn)) {
//            println(this.draw())
            count++
        }
        return count
    }

    fun fillOneSand(start: Pos): Boolean {
        putTile(start, Tile.SAND)
        var pos = start
        while (true) {
            val moved = tryMoveSandFrom(pos) ?: return false // VOID
            if (moved == pos) {
                return true // resting
            } else {
                pos = moved // moving
            }
        }
    }

    fun tryMoveSandFrom(from: Pos): Pos? {
        for (dir in Direction.values()) {
            val dest = dir.apply(from)
            if (!bounds.contains(dest)) {
                putTile(from, Tile.AIR)
                return null // VOID
            }
            if (canMoveSandTo(dest)) {
                putTile(from, Tile.AIR)
                putTile(dest, Tile.SAND)
                return dest
            }
        }
        return from
    }

    fun canMoveSandTo(pos: Pos): Boolean =
        when (tileAt(pos)) {
            Tile.ROCK -> false
            Tile.AIR -> true
            Tile.SAND -> false
        }

    override fun toString(): String = "$bounds ${tiles.size}"

    fun draw(): String {
        val sb = StringBuffer()
        for (y in bounds.min.y..bounds.max.y) {
            for (x in bounds.min.x..bounds.max.x) {
                sb.append(tileAt(Pos(x, y)).c)
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

data class Shape(val lines: List<Line>) {
    fun bounds() = Bounds(
        lines.fold(POUR_IN) { min, line ->
            min.min(line.start).min(line.end)
        },
        lines.fold(POUR_IN) { max, line ->
            max.max(line.start).max(line.end)
        }
    )
}

data class Bounds(val min: Pos, val max: Pos) {
    fun contains(pos: Pos): Boolean =
        pos.x >= min.x && pos.x <= max.x &&
                pos.y >= min.y && pos.y <= max.y

    fun extend(other: Bounds) = Bounds(
        min.min(other.min),
        max.max(other.max)
    )

    override fun toString(): String = "Bounds($min - $max)"
}

enum class Tile(val c: Char) {
    ROCK('#'),
    AIR('.'),
    SAND('o')
}

enum class Direction(val moveDelta: Pos) {
    DOWN(Pos(0, 1)),
    DOWN_LEFT(Pos(-1, 1)),
    DOWN_RIGHT(Pos(1, 1));

    fun apply(pos: Pos): Pos = pos + moveDelta
}

data class Pos(val x: Int, val y: Int) : Comparable<Pos> {
    operator fun plus(move: Pos): Pos = Pos(
        this.x + move.x,
        this.y + move.y
    )

    fun delta(other: Pos): Pos = Pos(
        other.x - this.x,
        other.y - this.y
    )

    fun clampToSignedOne() = Pos(
        x.coerceIn(-1, 1),
        y.coerceIn(-1, 1)
    )

    fun min(other: Pos): Pos = Pos(
        minOf(this.x, other.x),
        minOf(this.y, other.y)
    )

    fun max(other: Pos): Pos = Pos(
        maxOf(this.x, other.x),
        maxOf(this.y, other.y)
    )

    override fun compareTo(other: Pos): Int {
        val delta = delta(other).clampToSignedOne()
        if (delta.x == -1 || delta.y == -1) return -1
        if (delta.x == 1 || delta.y == 1) return 1
        println("Pos.compareTo REALLY 0? $delta")
        return 0
    }

    override fun toString(): String = "($x/$y)"

    companion object {
        fun from(s: String): Pos {
            val ps = s.split(",")
            return Pos(ps[0].toInt(), ps[1].toInt())
        }
    }
}

sealed interface Line : Iterable<Pos> {
    val start: Pos
    val end: Pos

    data class XLine(val x: IntProgression, val y: Int) : Line {
        override val start: Pos
            get() = Pos(x.first, y)

        override val end: Pos
            get() = Pos(x.last, y)

        override fun iterator(): Iterator<Pos> = Iter(x.iterator(), y)

        class Iter(val x: Iterator<Int>, val y: Int) : Iterator<Pos> {
            override fun hasNext(): Boolean = x.hasNext()
            override fun next(): Pos = Pos(x.next(), y)
        }

        override fun toString(): String = "XRange(${x.first}..${x.last}, $y)"
    }

    data class YLine(val x: Int, val y: IntProgression) : Line {
        override val start: Pos
            get() = Pos(x, y.first)

        override val end: Pos
            get() = Pos(x, y.last)

        override fun iterator(): Iterator<Pos> = Iter(x, y.iterator())

        class Iter(val x: Int, val y: Iterator<Int>) : Iterator<Pos> {
            override fun hasNext(): Boolean = y.hasNext()
            override fun next(): Pos = Pos(x, y.next())
        }

        override fun toString(): String = "VertLine($x, ${y.first}..${y.last})"
    }

    companion object {
        fun from(a: Pos, b: Pos): Line {
            require((a.x == b.x).xor(a.y == b.y)) { "$a / $b" }
            return if (a.x == b.x) {
                if (a.y < b.y)
                    YLine(a.x, a.y.rangeTo(b.y))
                else
                    YLine(a.x, a.y.downTo(b.y))
            } else {
                if (a.x < b.x)
                    XLine(a.x.rangeTo(b.x), a.y)
                else
                    XLine(a.x.downTo(b.x), a.y)
            }
        }
    }
}
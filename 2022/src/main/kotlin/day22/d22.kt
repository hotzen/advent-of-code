package day22

import util.getResourceAsFile

fun main() {
    val (board, start, path) = getResourceAsFile("day22.txt").useLines { lines ->
        parse(lines)
    }
//    println(board)
//    println(path)

    val (pos, facing) = board.follow(path, start, Facing.RIGHT)
    val facingVal = Facing.values().indexOf(facing)
    val password = (1000 * (pos.y + 1)) + (4 * (pos.x + 1)) + facingVal

    println(pos)
    println("$facing / $facingVal")
    println(password)
}

data class Board(val tiles: Map<Pos, Tile>) {
    fun tileAt(pos: Pos): Tile = tiles.get(pos) ?: Tile.VOID

    fun follow(path: List<PathOp>, startPos: Pos, startFacing: Facing): Pair<Pos, Facing> {
        var pos = startPos
        var facing = startFacing
        for (op in path) {
            when (op) {
                is PathOp.Move -> {
//                    println("GO ${op.tiles} from $pos $facing")
                    val (newPos, newFacing) = go(op.tiles, pos, facing)
                    pos = newPos
                    facing = newFacing
                }

                PathOp.TurnLeft -> {
                    val newFacing = facing.turnLeft()
//                    println("TURN LEFT, $facing to $newFacing")
                    facing = newFacing
                }

                PathOp.TurnRight -> {
                    val newFacing = facing.turnRight()
//                    println("TURN RIGHT, $facing to $newFacing")
                    facing = newFacing
                }
            }
        }
        return pos to facing
    }

    fun go(steps: Int, startPos: Pos, startFacing: Facing): Pair<Pos, Facing> {
        var pos = startPos
        var facing = startFacing

        for (step in 1..steps) {
            val nextPos = pos.next(startFacing)
            when (tileAt(nextPos)) {
                Tile.VOID -> {
                    val wrappedPos = wrap(pos, facing)
                    if (wrappedPos == null) {
                        return pos to facing
                    } else {
                        val stepsLeft = steps - step
//                        println("WRAP: from $pos to $wrappedPos, continue with $stepsLeft")
                        return go(stepsLeft, wrappedPos, facing)
                    }
                }

                Tile.FREE -> {
//                    println(nextPos)
                    pos = nextPos
                }

                Tile.WALL -> {
//                    println("WALL")
                    return pos to facing
                }
            }
        }
        return pos to facing
    }

    private fun wrap(startPos: Pos, startFacing: Facing): Pos? {
        var pos = startPos
        val oppositeFacing = startFacing.opposite()
        while (true) {
            val nextPos = pos.next(oppositeFacing)
            val nextTile = tileAt(nextPos)
            when (nextTile) {
                Tile.FREE, Tile.WALL -> pos = nextPos
                Tile.VOID -> {
                    return when (tileAt(pos)) {
                        Tile.FREE -> pos
                        Tile.WALL -> null
                        Tile.VOID -> throw IllegalStateException("nope")
                    }
                }
            }
        }
    }
}

data class Pos(val x: Int, val y: Int) {
    fun next(facing: Facing): Pos =
        when (facing) {
            Facing.RIGHT -> Pos(x + 1, y)
            Facing.LEFT -> Pos(x - 1, y)
            Facing.UP -> Pos(x, y - 1)
            Facing.DOWN -> Pos(x, y + 1)
        }

    override fun toString(): String = "(${x + 1}/${y + 1})"
}

enum class Tile { WALL, FREE, VOID }

enum class Facing {
    // Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
    RIGHT, DOWN, LEFT, UP;

    fun opposite(): Facing =
        when (this) {
            RIGHT -> LEFT
            LEFT -> RIGHT
            UP -> DOWN
            DOWN -> UP
        }

    fun turnRight(): Facing =
        when (this) {
            RIGHT -> DOWN
            LEFT -> UP
            UP -> RIGHT
            DOWN -> LEFT
        }

    fun turnLeft(): Facing =
        when (this) {
            RIGHT -> UP
            LEFT -> DOWN
            UP -> LEFT
            DOWN -> RIGHT
        }
}


sealed interface PathOp {
    @JvmInline
    value class Move(val tiles: Int) : PathOp
    object TurnRight : PathOp
    object TurnLeft : PathOp
}

fun parse(lines: Sequence<String>): Triple<Board, Pos, List<PathOp>> {
    val tiles = mutableMapOf<Pos, Tile>()
    var startPos: Pos? = null
    val path = mutableListOf<PathOp>()
    var isTiles = true
    val pathSplitter = "(?<=[RL])|(?=[RL])".toRegex()

    lines.forEachIndexed { y, line ->
        if (line.isEmpty()) {
            isTiles = false
        } else if (isTiles) {
            line.toCharArray().forEachIndexed { x, c ->
                val tile = when (c) {
                    '.' -> Tile.FREE
                    '#' -> Tile.WALL
                    else -> null
                }
                if (tile != null) {
                    val pos = Pos(x, y)
                    tiles[pos] = tile
                    if (startPos == null) {
                        startPos = pos
                    }
                }
            }
        } else {
            pathSplitter.split(line).forEach { part ->
                path.add(
                    when (part) {
                        "R" -> PathOp.TurnRight
                        "L" -> PathOp.TurnLeft
                        else -> PathOp.Move(part.toInt())
                    }
                )
            }
        }
    }
    return Triple(
        Board(tiles.toMap()),
        startPos!!,
        path.toList()
    )
}
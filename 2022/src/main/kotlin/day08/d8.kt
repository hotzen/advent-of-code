package day08

import util.getResourceAsFile

fun main() {
    getResourceAsFile("day08.txt").useLines { lines ->
        val p = TreePatch.from(lines)
        part1(p)
        part2(p)
    }
}

fun part1(p: TreePatch) {
    val vis = buildSet {
        for (c in p.cols()) {
            addAll(p.findVisibleTrees(TreePos(c, 0), Direction.DOWN))
            addAll(p.findVisibleTrees(TreePos(c, p.height - 1), Direction.UP))
        }
        for (r in p.rows()) {
            addAll(p.findVisibleTrees(TreePos(p.width - 1, r), Direction.LEFT))
            addAll(p.findVisibleTrees(TreePos(0, r), Direction.RIGHT))
        }
    }
    println(vis.size)
}

fun part2(p: TreePatch) {
    val scenicScores = p.mapTrees { pos, tree ->
        Direction.values()
            .map { dir -> p.determineViewingDistance(pos, dir) }
            .reduce { acc, i -> acc * i }
    }
    println(
        scenicScores.max()
    )
}

class TreePatch(val width: Int, val height: Int, private val data: ArrayList<Tree>) {
    fun cols() = IntRange(0, width - 1)
    fun rows() = IntRange(0, height - 1)

    fun <T> mapTrees(f: (TreePos, Tree) -> T): List<T> {
        val res = mutableListOf<T>()
        for (c in 0 until width) {
            for (r in 0 until height) {
                val pos = TreePos(c, r)
                val tree = treeAt(pos)
                res.add(f(pos, tree))
            }
        }
        return res
    }

    fun findVisibleTrees(start: TreePos, dir: Direction): Set<TreePos> {
//        println("findVisibleTrees from $start in $dir direction")
        var coord = start
        var maxTree: Tree? = null
        val visibleTrees = mutableSetOf<TreePos>()

        while (coord.isWithin(this)) {
            val tree = treeAt(coord)
            if (maxTree == null || tree > maxTree) {
                visibleTrees.add(coord)
                maxTree = tree
            }
            coord = coord.move(dir)
        }
        return visibleTrees
    }

    fun determineViewingDistance(fromPos: TreePos, dir: Direction): Int {
        val fromTree = treeAt(fromPos)
//        println("findViewableTrees from $fromPos / $fromTree in $dir direction...")
        var dist = 0
        fromPos.moves(dir, this).forEach { pos ->
            dist++
            val tree = treeAt(pos)
            if (tree >= fromTree) {
                return dist
            }
        }
        return dist
    }

    fun treeAt(pos: TreePos): Tree = data[(pos.row * width) + pos.col]

    companion object {
        fun from(lines: Sequence<String>): TreePatch {
            val data = ArrayList<Tree>()
            var lineWidth = -1
            var lineCount = 0
            lines.forEach { line ->
                lineWidth = line.length
                lineCount++
                line.forEach { tree ->
                    data.add(Tree.from(tree))
                }
            }
            return TreePatch(lineWidth, lineCount, data)
        }
    }

    override fun toString(): String = "Tree($width *  $height = ${data.size})"
}

enum class Direction(val moveCol: Int, val moveRow: Int) {
    DOWN(0, 1),
    UP(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0)
}

data class TreePos(val col: Int, val row: Int) {
    fun move(dir: Direction): TreePos = TreePos(col + dir.moveCol, row + dir.moveRow)

    fun moves(dir: Direction, p: TreePatch): List<TreePos> = buildList {
        var nextPos = this@TreePos.move(dir)
        while (nextPos.isWithin(p)) {
            add(nextPos)
            nextPos = nextPos.move(dir)
        }
    }

    fun isWithin(patch: TreePatch): Boolean = col >= 0
            && col < patch.width
            && row >= 0
            && row < patch.height
}

@JvmInline
value class Tree(val height: Int) : Comparable<Tree> {
    companion object {
        fun from(tree: Char): Tree = Tree(tree.digitToInt())
    }

    override fun compareTo(other: Tree): Int = this.height.compareTo(other.height)
}
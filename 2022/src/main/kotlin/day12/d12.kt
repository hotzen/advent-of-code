package day12

import util.getResourceAsFile

fun main() {
    val hill = getResourceAsFile("day12.txt").useLines { Hill.parse(it) }
    println("${hill.start} ${hill.end}")
    val graph = Graph.build(hill)
//    println("${graph.verts}")//    println("${graph.edges}")

    val shortestPath = graph.dijkstra(hill.start, hill.end)
    val steps = shortestPath.size - 1

//    println("from ${hill.start} to ${hill.end}:\n" + shortestPath.joinToString("\n"))
    println("### $steps")
}

data class Hill(
    val cols: Int,
    val rows: Int,
    val heightmap: Map<Pos, Int>,
    val start: Pos,
    val end: Pos
) {
    companion object {
        fun parse(lines: Sequence<String>): Hill {
            val heightsBySymbol = buildMap<Char, Int> {
                putAll(('a'..'z').mapIndexed { idx, c -> Pair(c, idx) }.associate { it })
                put('S', get('a')!!)
                put('E', get('z')!!)
            }

            var rows = 0
            var cols = -1
            var start: Pos? = null
            var end: Pos? = null

            val heightmap = lines.flatMapIndexed { row, line ->
                rows++
                cols = line.length
                line.toCharArray().mapIndexed { col, c ->
                    val pos = Pos(col, row)
                    if (c == 'S') start = pos
                    if (c == 'E') end = pos
                    pos to heightsBySymbol[c]!!
                }
            }.associate { it }

            return Hill(cols, rows, heightmap, start!!, end!!)
        }
    }

    fun heightAt(pos: Pos): Int = heightmap[pos]!!
}

data class Pos(val col: Int, val row: Int) {
    fun neighbors(hill: Hill): Set<Pos> = buildSet {
        if (row + 1 < hill.rows)
            add(Pos(col, row + 1))
        if (row - 1 >= 0)
            add(Pos(col, row - 1))
        if (col + 1 < hill.cols)
            add(Pos(col + 1, row))
        if (col - 1 >= 0)
            add(Pos(col - 1, row))
    }
}

data class Graph(
    val verts: Set<Pos>,
    val edges: Map<Pos, Set<Pos>>,
    val rises: Map<Pair<Pos, Pos>, Int>
) {
    fun dijkstra(start: Pos, end: Pos): List<Pos> {
        val visited = mutableSetOf<Pos>()
        val dist = verts.associateWith { Int.MAX_VALUE shr 1 }.toMutableMap()
        val prev = mutableMapOf<Pos, Pos>()

        dist[start] = 0

        while (visited != verts) {
            val u = dist
                .filter { !visited.contains(it.key) }
                .minBy { it.value }
                .key
            visited.add(u)

            (edges[u]!! - visited).forEach { v ->
                val cur = dist[v]!!
                val rise = rises[u to v]!!
                val alt = dist[u]!! + rise
                check(alt > 0) {
                    "U $u -> V $v: cur $cur / alt $alt because rise $rise"
                }
                if (alt < cur) {
                    dist[v] = alt
                    prev[v] = u
                }
            }

        }
        return buildPathFrom(prev, start, end)
    }

    private fun buildPathFrom(prev: MutableMap<Pos, Pos>, from: Pos, to: Pos): List<Pos> {
        return if (prev[to] == null) {
            listOf(to)
        } else {
            buildPathFrom(prev, from, prev[to]!!) + listOf(to)
        }
    }

//    private fun buildPathFrom2(prev: MutableMap<Pos, Pos>, start: Pos, end: Pos): List<Pos> {
//        val s = mutableListOf<Pos>()
//        var u: Pos? = end
//
//        if (prev[u] != null || u == start) {
//            while (u != null) {
//                s.add(u)
//                u = prev[u]
//            }
//        }
//        return s.reversed()
//    }


    companion object {
        fun build(hill: Hill): Graph {
            val verts = mutableSetOf<Pos>()
            val edges = mutableMapOf<Pos, MutableSet<Pos>>()
            val dist = mutableMapOf<Pair<Pos, Pos>, Int>()
            hill.heightmap.map { (pos, height) ->
                verts.add(pos)

                pos.neighbors(hill).forEach { neighbor ->
                    val d = hill.heightAt(neighbor) - height
                    if (d <= 1) {
                        if (edges.contains(pos)) {
                            edges[pos]!!.add(neighbor)
                        } else {
                            edges[pos] = mutableSetOf(neighbor)
                        }
                        dist[Pair(pos, neighbor)] = 1
                    }
                }
            }
            return Graph(verts, edges, dist)
        }
    }
}
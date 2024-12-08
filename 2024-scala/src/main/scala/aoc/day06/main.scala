package day06

import scala.io.Source
import aoc.Pos
import scala.collection.mutable
import aoc.PosExt.*

val in = Source
  .fromFile("input/day06.txt")
  .mkString
  .trim

@main def main(): Unit = {
  var guardPos: aoc.Pos = null
  var dim: aoc.Pos = (0, 0)

  val map: Set[Pos] = in.linesIterator.zipWithIndex
    .flatMap((line, y) =>
      line.zipWithIndex.flatMap((c, x) =>
        val pos = (x, y)
        dim = dim.max(pos)
        c match {
          case '#' => Some(pos)
          case '^' => guardPos = pos; None
          case _   => None
        }
      )
    )
    .toSet

  part1(guardPos, aoc.Dir.Up, map, dim)
  part2(guardPos, aoc.Dir.Up, map, dim)
}

def findPath(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Map[Pos, Pos] = {
  val path = mutable.Map[Pos, Pos]()

  var curPos = guardPos
  var curDir = guardDir

  while (curPos.within(dim)) {
    path.addOne(curPos, curDir)
    val nextPos = curPos.go(curDir)
    if (obstacles.contains(nextPos)) {
      curDir = curDir match {
        case aoc.Dir.Up    => aoc.Dir.Right
        case aoc.Dir.Down  => aoc.Dir.Left
        case aoc.Dir.Left  => aoc.Dir.Up
        case aoc.Dir.Right => aoc.Dir.Down
      }
    } else {
      curPos = nextPos
    }
  }
  path.toMap
}

def findLoop(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Boolean = {
  val path = mutable.Map[Pos, List[Pos]]() // position => directions

  var curPos = guardPos
  var curDir = guardDir

  while (curPos.within(dim)) {
    path(curPos) = path.getOrElse(curPos, List()) :+ curDir

    val nextPos = curPos.go(curDir)

    path.get(nextPos) match {
      case Some(pathDirs) if pathDirs.contains(curDir) => return true
      case Some(pathDirs) => // println(s"crossing path at $nextPos with new direction $curDir")
      case None           => // not crossed yet
    }

    if (obstacles.contains(nextPos)) {
      curDir = curDir match {
        case aoc.Dir.Up    => aoc.Dir.Right
        case aoc.Dir.Down  => aoc.Dir.Left
        case aoc.Dir.Left  => aoc.Dir.Up
        case aoc.Dir.Right => aoc.Dir.Down
      }
    } else {
      curPos = nextPos
    }
  }
  false
}

def part1(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Unit = {
  val path = findPath(guardPos, guardDir, obstacles, dim)
  println(path.size)
}

def part2(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Unit = {
  val path = findPath(guardPos, guardDir, obstacles, dim)
  val obstacleCandidates = path.keys.flatMap(_.around().filterNot(_ == guardPos)).toList
  val loops = obstacleCandidates.map(obs => findLoop(guardPos, guardDir, obstacles + obs, dim))
  println(loops.count(_ == true))
}

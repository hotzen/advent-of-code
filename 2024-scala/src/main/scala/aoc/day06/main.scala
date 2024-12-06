package day06

import scala.io.Source
import aoc.Pos
import scala.collection.mutable
import aoc.max
import aoc.isWithin
import aoc.go

val in = Source
  .fromFile("input/day06.txt")
  .mkString
  .trim

@main def main(): Unit = {
  var guardPos: aoc.Pos = null
  var dim: aoc.Pos = (0,0)

  val map: Set[Pos] = in.linesIterator.zipWithIndex.flatMap( (line, y) =>
    line.zipWithIndex.flatMap( (c, x) => 
      val pos = (x,y)
      dim = dim.max(pos)
      c match {
        case '#' => Some(pos)
        case '^' => guardPos = pos; None
        case  _ => None
      }
    )
  ).toSet
  
  part1(guardPos, aoc.Dir.Up, map, dim)
  part2(guardPos, aoc.Dir.Up, map, dim)
}

def part1(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Unit = {
  val path = mutable.Set[Pos]()

  var curPos = guardPos
  var curDir = guardDir

  while (curPos.isWithin(dim)) {
    path.addOne(curPos)
    val nextPos = curPos.go(curDir)
    if (obstacles.contains(nextPos)) {
      curDir = curDir match {
        case aoc.Dir.Up => aoc.Dir.Right
        case aoc.Dir.Down => aoc.Dir.Left
        case aoc.Dir.Left => aoc.Dir.Up
        case aoc.Dir.Right => aoc.Dir.Down
      }
    } else {
      curPos = nextPos
    }
  }

  println(path.size)
}

def part2(guardPos: Pos, guardDir: Pos, obstacles: Set[Pos], dim: Pos): Unit = {
  val visited = mutable.Set[Pos]()
  val path = mutable.ListBuffer[Pos]()

  var curPos = guardPos
  var curDir = guardDir

  while (curPos.isWithin(dim)) {
    path.addOne(curPos)
    val nextPos = curPos.go(curDir)
    if (obstacles.contains(nextPos)) {
      curDir = curDir match {
        case aoc.Dir.Up => aoc.Dir.Right
        case aoc.Dir.Down => aoc.Dir.Left
        case aoc.Dir.Left => aoc.Dir.Up
        case aoc.Dir.Right => aoc.Dir.Down
      }
    } else {
      curPos = nextPos
    }
  }

  println(path.size)
}

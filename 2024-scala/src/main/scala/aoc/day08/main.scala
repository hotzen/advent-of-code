package day08

import scala.io.Source
import scala.collection.mutable
import aoc.Pos
import aoc.PosExt.*

val in = Source
  .fromFile("input/day08.txt")
  .mkString
  .trim

@main def main(): Unit = {
  val antennas = mutable.Map[Char, mutable.ListBuffer[Pos]]()
  var dim: Pos = (0,0)

  for (
    (line, y) <- in.linesIterator.zipWithIndex;
    (c, x) <- line.zipWithIndex 
  ) {
    val pos = (x, y)
    dim = dim.max(pos)
    if (c != '.') {
      antennas.getOrElseUpdate(c, mutable.ListBuffer[Pos]()).addOne(pos)
    }
  }

  val antinodes = mutable.Set[Pos]()
  for (
    (freq, poss) <- antennas;
    pos <- poss.sortWith(cmp)
  ) {
    val others = poss.filterNot(_ == pos)
    for (otherPos <- others) {
      // val d = otherPos.delta(pos)
      // List(
      //   pos - d,
      //   otherPos + d
      // ).filter(_ within dim)
      // .foreach(antinodes.addOne(_))

      antinodes.addAll(
        calcInLinePos(
          pos, otherPos.delta(pos), dim
        )
      )
    }
  }
  println(antinodes.size)
}

def calcInLinePos(pos: Pos, delta: Pos, dim: Pos): List[Pos] = {
  var p = pos
  val ps = mutable.ListBuffer[Pos]()
  
  while (p within dim) {
    ps.addOne(p)
    p = p - delta
  }

  p = pos + delta
  while (p within dim) {
    ps.addOne(p)
    p = p + delta
  }

  ps.toList
}
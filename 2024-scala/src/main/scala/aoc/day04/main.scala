package day04

import scala.io.Source
import scala.collection.mutable

val in = Source
  .fromFile("input/day04.txt")
  .getLines
  .zipWithIndex

val UpLeft = (-1, -1)
val UpRight = (1, -1)
val DownLeft = (-1, 1)
val DownRight = (1, 1)

val dirs = List(
  (0, -1), // Up
  (0, 1), // Down
  (-1, 0), // Left
  (1, 0), // Right
  UpLeft,UpRight,
  DownLeft,DownRight
)


def applyDir(pos: (Int, Int), dir: (Int, Int)): (Int, Int) = (pos._1 + dir._1, pos._2 + dir._2)

extension (pos: (Int,Int))
  def dir(dir: (Int, Int)): (Int, Int) = applyDir(pos, dir)

val xmas = "XMAS".toCharArray

def checkXmasSeq(startPos: (Int, Int), dir: (Int, Int), chars: List[Char], map: Map[(Int,Int),Char]): Boolean = {
  if (chars.isEmpty)
    return true
  
  val nextPos = applyDir(startPos, dir)
  val nextChar = chars.head

  map.get(nextPos) match {
    case Some(c) if c == nextChar => checkXmasSeq(nextPos, dir, chars.tail, map)
    case Some(c)                  => false
    case None                     => false
  }
}


@main def part1(): Unit = {
  val _map = mutable.Map[(Int,Int),Char]()
  val xs = mutable.ListBuffer[(Int,Int)]()

  for ((line, y) <- in) {
    for ((char, x) <- line.toCharArray().zipWithIndex) {
      val pos = (x,y)
      char match {
        case 'X' => xs += pos
        case 'M' => _map += pos -> char
        case 'A' => _map += pos -> char
        case 'S' => _map += pos -> char
      }
    }
  }
  val map = _map.toMap

  val mas = xmas.toList.tail
  val checked = for {
    xPos <- xs
    dir <- dirs
  } yield checkXmasSeq(xPos, dir, mas, map)

  val count = checked.count(checked => checked)
  println(count)
}

extension (map: Map[(Int,Int), Char])
  def check(pos: (Int, Int), char: Char): Boolean = map.get(pos) match {
    case Some(c) if c == char => true
    case Some(c)              => false
    case None                 => false
  }


def checkMasCross(aPos: (Int, Int),map: Map[(Int,Int),Char]): Boolean = {
  val left = 
    (map.check(aPos.dir(UpLeft), 'M') && map.check(aPos.dir(DownRight), 'S'))
    || (map.check(aPos.dir(UpLeft), 'S') && map.check(aPos.dir(DownRight), 'M'))

  val right = 
    (map.check(aPos.dir(UpRight), 'M') && map.check(aPos.dir(DownLeft), 'S'))
    || (map.check(aPos.dir(UpRight), 'S') && map.check(aPos.dir(DownLeft), 'M'))
  
  left && right
}

@main def part2(): Unit = {
  val _map = mutable.Map[(Int,Int),Char]()
  val as = mutable.ListBuffer[(Int,Int)]()

  for ((line, y) <- in) {
    for ((char, x) <- line.toCharArray().zipWithIndex) {
      val pos = (x,y)
      char match {
        case 'X' => // ignore
        case 'M' => _map += pos -> char
        case 'A' => as += pos
        case 'S' => _map += pos -> char
      }
    }
  }
  val map = _map.toMap

  val checked = for {
    aPos <- as
  } yield {
    checkMasCross(aPos, map)
  }

  val count = checked.count(checked => checked)
  println(count)
}

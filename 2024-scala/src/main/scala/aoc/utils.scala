package aoc

extension (s: String)
  def splitIntoTuple(sep: String): (String, String) = {
    val split = s.split(sep)
    assert(split.size == 2, s"split into parts of size ${split.size} not 2")
    (split(0), split(1))
  }

extension (s: String)
  def splitIntoIntTuple(sep: String): (Int, Int) = {
    val split = s.split(sep).toList
    assert(split.size == 2, s"split into parts of size ${split.size} not 2")
    (split(0).toInt, split(1).toInt)
  }

extension (arr: Array[Int])
  def midElem: Int = arr(arr.size / 2)

type Pos = (Int, Int)

object Dir {
  val Up = (0, -1)
  val Down = (0, 1)
  
  val Left = (-1, 0)
  val Right = (1, 0)
  
  val UpLeft = (-1, -1)
  val UpRight = (1, -1)
  val DownLeft = (-1, 1)
  val DownRight = (1, 1)
}

object PosExt {
  def cmp(a: Pos, b: Pos): Boolean = {
    if (a._2 != b._2)
        a._2 < b._2
      else
        a._1 < b._1
  }

  extension (pos: Pos)
    infix def go(dir: Pos): Pos = (pos._1 + dir._1, pos._2 + dir._2)

    infix def max(other: Pos): Pos = (pos._1 max other._1, pos._2 max other._2)
    
    infix def +(other: Pos): Pos = (pos._1 + other._1, pos._2 + other._2)
    infix def -(other: Pos): Pos = (pos._1 - other._1, pos._2 - other._2)

    infix def within(dim: Pos): Boolean =
      pos._1 >= 0 && pos._1 <= dim._1 &&
      pos._2 >= 0 && pos._2 <= dim._2

    def around(): List[Pos] = List(
      pos.go(Dir.Up),
      pos.go(Dir.Down),
      pos.go(Dir.Left),
      pos.go(Dir.Right),
    )

    def delta(other: Pos): Pos = (
      (pos._1 - other._1),
      (pos._2 - other._2)
    )
}


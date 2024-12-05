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
  def midElem(): Int = arr(arr.size / 2)
  
package day03

import scala.io.Source
import scala.collection.immutable.TreeMap

val in = Source
  .fromFile("input/day03.txt")
  .mkString
  .trim()

val mulPattern = """mul\((\d+),(\d+)\)""".r

val doDontPattern = """do\(\)|don't\(\)""".r

case class MulOp(val a: Int, val b: Int) {
  def apply(): Int = a * b
}

case class DoDont(val x: Boolean)

@main def part(): Unit = {

  val doDonts: Seq[Tuple2[Int, DoDont]] = (
    for doDontMatch <- doDontPattern.findAllMatchIn(in) yield
      doDontMatch.start -> DoDont(doDontMatch.matched == "do()")
  ).toSeq
  println(doDonts.mkString(", "))

  val muls: Seq[Tuple2[Int, MulOp]] = (
    for mulMatch <- mulPattern.findAllMatchIn(in) yield
      mulMatch.start -> MulOp(mulMatch.group(1).toInt, mulMatch.group(2).toInt)
  ).toSeq
  println(muls.mkString(", "))

  val leftDoDonts: Seq[Tuple2[Int, Either[DoDont, MulOp]]] = doDonts.map {
    case (pos, doDont) => pos -> Left(doDont)
  }
  val rightMulOps: Seq[Tuple2[Int, Either[DoDont, MulOp]]] = muls.map {
    case (pos, mul) => pos -> Right(mul)
  }

  val all = TreeMap(leftDoDonts ++ rightMulOps*)
  
  var doIt = true
  val vs = all.values.map {
    case Left(DoDont(true)) => doIt = true; 0
    case Left(DoDont(false)) => doIt = false; 0
    case Right(m) => if (doIt) m.apply() else 0
  }
  println(vs.sum)
}

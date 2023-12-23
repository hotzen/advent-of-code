import scala.io.Source
import java.util.LinkedList
import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer

implicit class RangeExt(range1: Range) {
  def intersects(range2: Range): Boolean = {
    range1.start <= range2.end && range2.start <= range1.end
  }
}

case class PosRange(xs: Range, ys: Range, zs: Range) {
  def isDirectlyAbove(other: PosRange): Boolean = {
    (zs.start == other.zs.last + 1)
  }

  def isDirectlyBelow(other: PosRange): Boolean = {
    (zs.last == other.zs.start - 1)
  }

//   def isAbove(other: PosRange): Boolean =
//     (other.zs.end > this.zs.start)

  def intersectsWith(other: PosRange): Boolean = {
    xs.intersects(other.xs) && ys.intersects(other.ys)
  }

  def collidesWith(other: PosRange): Boolean = {
    intersectsWith(other) &&
    (zs.start <= other.zs.end && other.zs.start <= zs.end)
  }

  def collidesWithAny(others: Seq[PosRange]): Boolean = {
    others.exists(collidesWith)
  }

  def isWithinBounds(bounds: PosRange): Boolean = {
    (xs.start >= bounds.xs.start && xs.last <= bounds.xs.last) &&
    (ys.start >= bounds.ys.start && ys.last <= bounds.ys.last) &&
    (zs.start >= bounds.zs.start && zs.last <= bounds.zs.last)
  }

  def fallOne() = PosRange(
    xs,
    ys,
    zs.start - 1 to zs.end - 1
  )

  override def toString(): String =
    s"(${xs.start},${ys.start},${zs.start}~${xs.last},${ys.last},${zs.last})"
}

object PosRange {
  def from(s: String): PosRange = {
    val Array(from, to) = s.split("~")
    val Array(fx, fy, fz) = from.split(",").map(_.toInt)
    val Array(tx, ty, tz) = to.split(",").map(_.toInt)
    PosRange(fx to tx, fy to ty, fz to tz)
  }
}

val in = Source
  .fromFile("input/day22ex.txt")
  .mkString
  .trim()
  .linesIterator
  .map(PosRange.from(_))
  .toList

val bounds = in.fold(
  PosRange(
    Int.MaxValue to Int.MinValue,
    Int.MaxValue to Int.MinValue,
    1 to Int.MinValue // the lowest z value a brick can have is therefore 1
  )
)((a, b) =>
  PosRange(
    (a.xs.start min b.xs.start) to (a.xs.end max b.xs.end),
    (a.ys.start min b.ys.start) to (a.ys.end max b.ys.end),
    (a.zs.start min b.zs.start) to (a.zs.end max b.zs.end)
  )
)

def fall(in: List[PosRange]): List[PosRange] = {
  val q = Queue[PosRange](in.sortBy(_.zs.end): _*) // lowest first
  val fallen = ListBuffer[PosRange]()

  while (q.nonEmpty) {
    fallen += fallBrick(q.dequeue(), fallen.toSeq)
  }
  fallen.toList
}

def fallBrick(brick: PosRange, others: Seq[PosRange]): PosRange = {
  var falling = brick
  while (true) {
    val fallen = falling.fallOne()

    if (!fallen.isWithinBounds(bounds)) {
      //   println(s"$brick finished falling at $falling (next $fallen is already out of bounds)")
      return falling
    }

    if (fallen.collidesWithAny(others)) {
      //   println(s"$brick finished falling at $falling (next $fallen is already colliding)")
      return falling
    }

    // println(s"$brick fell to $fallen and continues to fall ...")
    falling = fallen
  }
  ???
}

def hasOtherSupporters(brick: PosRange, all: Seq[PosRange]): Boolean = {
  val tops = all
    .filter(_.isDirectlyAbove(brick))
    .filter(_.intersectsWith(brick))

  if (tops.isEmpty)
    return true

  val topsWithOtherSupport = tops.map(top =>
    all
      .filter(_.isDirectlyBelow(top))
      .filter(_.intersectsWith(top))
      .find(_ != brick)
  )

  topsWithOtherSupport.forall(sup => sup.isDefined)
}

def part1(): Unit = {
  val fallen = fall(in)
  println(fallen.count(hasOtherSupporters(_, fallen)))
}

def desintegrateChain(brick: PosRange, all: Set[PosRange]): Set[PosRange] = {
  val tops = all
    .filter(_.isDirectlyAbove(brick))
    .filter(_.intersectsWith(brick))

  if (tops.isEmpty)
    return Set.empty

  val rem = all - brick -- tops

  val sups: Set[PosRange] = tops.flatMap(top =>
    val maybeOtherSup = rem
      .filter(_.isDirectlyBelow(top))
      .find(_.intersectsWith(top))

    maybeOtherSup match {
      case Some(otherSup) => Set.empty
      case None           => desintegrateChain(top, rem) + top
    }
  )
//   println(s"$brick: ${tops.zip(sups)}")

  sups
}

@main def part2(): Unit = {
  val fallen = fall(in).toSet
//   fallen.map(f => f -> chain(f, fallen)).foreach(println(_))
  println(fallen.map(desintegrateChain(_, fallen).size).sum)
  // 29847 too low
  // 23595 wtf
}

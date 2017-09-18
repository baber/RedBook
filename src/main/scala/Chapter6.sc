
import scala.collection.immutable.Stream._

def convertStreamToList[A](stream: Stream[A]): List[A] = {
  stream match {
    case Empty => Nil
    case #::(hd, tail) => hd :: convertStreamToList(tail)
  }
}

def take[A](stream: Stream[A], n: Int): Stream[A] = {
  (stream, n) match {
    case (Empty, _) | (_, 0) => Empty
    case _ => Stream.cons(stream.head, take(stream.tail, n - 1))
  }
}


def genSeqFromBinaryFunction[A, B](init: (A, B))(fn: ((A, B)) => (A, B)): Stream[B] = {
  Stream.cons(init._2, genSeqFromBinaryFunction(fn(init))(fn))
}

def genSeqFromUnaryFunction[A, B](init: A)(fn: A => (B, A)): Stream[B] = {
  val (value, newState) = fn(init)
  Stream.cons(value, genSeqFromUnaryFunction(newState)(fn))
}


def next(current: (Int, Long)): (Int, Long) = {
  val newSeed = (current._1 * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
  val n = (newSeed >>> 16).toInt
  (n, newSeed)
}


trait RNG {
  def nextInt: (Int, RNG)
}

case class SimpleRNG(current: Long) extends RNG {
  def nextInt: (Int, RNG) = {
    val newSeed = (current * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val n = (newSeed >>> 16).toInt
    (n, SimpleRNG(newSeed))
  }
}

def nextInt(current: Long): (Int, Long) = {
  val newSeed = (current * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
  val n = (newSeed >>> 16).toInt
  (n, newSeed)
}

def nonNegativeInt(rng: RNG): (Int, RNG) = {
  rng.nextInt
}


convertStreamToList(take(genSeqFromUnaryFunction(5L)(nextInt), 5))

def filter[A](f: A => Boolean, xs: List[A]) = {
  for (x <- xs; if f(x)) yield x
}


//filter((x: Double) => x > 5.0, List[Double](3,6,9,1))





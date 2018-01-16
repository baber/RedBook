
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


//convertStreamToList(take(genSeqFromUnaryFunction(5L)(nextInt), 5))


type Rand[+A] = RNG => (A, RNG)
val int: Rand[Int] = _.nextInt

def unit[A](a: A): Rand[A] =
  rng => (a, rng)

def map[A, B](rnd: Rand[A])(fn: A => B): Rand[B] = {
  rnd andThen (tup => (fn(tup._1), tup._2))
}

val prefixWithString = map(int)((a: Int) => "MAPPED_VALUE:" + a)
prefixWithString(SimpleRNG(1L))



def map2[A, B, C](rnd1: Rand[A], rnd2: Rand[B], fn: (A, B) => C): Rand[C] = {

  def myFunc: Rand[C] = {
    rng => {
      val a = rnd1(rng)
      val b = rnd2(a._2)
      val c = fn(a._1, b._1)
      (c, b._2)
    }
  }

  myFunc
}


val firstHalf: Rand[String] = { rng =>
  val value = rng.nextInt
  (s"FirstHalf ${value._1}", value._2)
}

val secondHalf: Rand[Int] = { rng =>
  val value = rng.nextInt
  (value._1, value._2)
}

def myFunc(a: String, b: Int): (String, Int) = {
  (a, b)
}

val myMappedRand = map2(firstHalf, secondHalf, myFunc)


def sequence[A](rands: List[Rand[A]]): Rand[List[A]] = {

  def foldStep(y: Rand[A], x: (RNG, List[A])): (RNG, List[A]) = {
    val newTuple = y(x._1)
    (newTuple._2, newTuple._1 :: x._2)
  }

  (rng: RNG) => {
    val sequencedResult = rands.foldRight(Tuple2(rng, List[A]()))(foldStep)
    (sequencedResult._2, sequencedResult._1)
  }

}


val firstName: Rand[String] = { rng =>
  val value = rng.nextInt
  (s"FirstName ${value._1}", value._2)
}


val secondName: Rand[String] = { rng =>
  val value = rng.nextInt
  (s"SecondName ${value._1}", value._2)
}

val thirdName: Rand[String] = { rng =>
  val value = rng.nextInt
  (s"ThirdName ${value._1}", value._2)
}


val mySequencer = sequence(List(firstName, secondName, thirdName))

//mySequencer(SimpleRNG(1L))._1

val nonNegativeInt: Rand[Int] = { rng =>
  rng.nextInt match {
    case (x, y) if x < 0 => (x * -1, y)
    case (x, y) => (x, y)
  }
}

val rng = SimpleRNG(-167L)
rng.nextInt._1
nonNegativeInt(SimpleRNG(-167L))


def mapWithRetry[A](x: Rand[A])(fn: A => Boolean): Rand[A] = {

  def fx(rng: RNG): (A, RNG) = {
    x(rng) match {
      case (v, r) => {
        if (fn(v)) (v, r) else fx(r)
      }
    }
  }

  fx
}

def isLessThanLastMultiple(ceiling: Int)(x: Int) = {
  x + (ceiling - 1) - (x % ceiling) >= 0
}

def nonNegativeLessThan(n: Int): Rand[Int] = {
  mapWithRetry(map(nonNegativeInt)(x => x % n))(isLessThanLastMultiple(n))

}


def randLessThan100 = nonNegativeLessThan(100)

randLessThan100(SimpleRNG(1L))








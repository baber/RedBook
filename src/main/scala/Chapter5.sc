

//def byNameFunc(x: Int) =  {
//  lazy val y = x
//  println(y)
//  println(y)
//  y + y
//}
//
//def randomNumber() = (math.random * 100).toInt
//byNameFunc(randomNumber)


//def twoParamFunc(x: Int)(y: Int) = {
//  x * y
//}
//
//val partiallyAppliedFuncAsVal = twoParamFunc(5)_
//def partiallyAppliedFuncAsDef = twoParamFunc(5)_
//
//partiallyAppliedFuncAsVal(10)
//partiallyAppliedFuncAsDef(10)
//
//
//trait MyTrait {
//  def bob: Int => Int
//}
//
//def jim(x: Int) = x
//
//def andrew(f: Int => Int) = f(5)
//andrew(jim)
//
//object defaultMyTrait extends MyTrait {
//  def bob = partiallyAppliedFuncAsVal
//}


// -------------------------------------------------------------------------


sealed trait Stream[+A]

case object Empty extends Stream[Nothing]

case class Cons[+A](head: () => A, tail: () => Stream[A]) extends Stream[A]

object Stream {

  def cons[A](value: => A, stream: => Stream[A]): Stream[A] = {
    lazy val head = value
    lazy val tail = stream

    Cons(() => head, () => tail)
  }

  def generate[A](n: Int, fn: () => A): Stream[A] = {
    if (n == 0) Empty else cons(fn(), generate(n - 1, fn))
  }

  def empty[A]: Stream[A] = Empty

  def take[A](n: Int, stream: Stream[A]): List[A] = {
    (n, stream) match {
      case (_, Empty) => Nil
      case (x, _) if x == 0 => Nil
      case (_, Cons(head, tail)) => head() :: take(n - 1, tail())
    }
  }

  def takeWhile[A](fn: A => Boolean, stream: Stream[A]): Stream[A] = {
    stream match {
      case Cons(head, tail) if fn(head()) => cons(head(), takeWhile(fn, tail()))
      case _ => Empty
    }
  }

  def convertStreamTolist[A](stream: Stream[A]): List[A] = {
    stream match {
      case Cons(head, tail) => head() :: convertStreamTolist(tail())
      case Empty => Nil
    }
  }

  def foldRight[A, B](acc: B)(fn: (A, => B) => B, stream: Stream[A]): B = {
    stream match {
      case Cons(head, tail) => fn(head(), foldRight(acc)(fn, tail()))
      case Empty => acc
    }
  }


  def headOption[A](stream: Stream[A]) : Option[A] = {
    stream match {
      case Empty => None
      case Cons(h, t) => Some(h())
    }
  }

  def headOptionWithFold[A](stream: Stream[A]) : Option[A] = {
    Stream.foldRight[A, Option[A]](None)((a, b) => Some(a), stream)
  }

  def map[A, B](fn: A => B)(stream: Stream[A]) : Stream[B] = {
    foldRight[A, Stream[B]](Empty)((a, b) => Stream.cons(fn(a), b), stream)
  }

  def filter[A](fn: A => Boolean)(stream: Stream[A]) : Stream[A] = {
    foldRight[A, Stream[A]](Empty)((a, b) => { if (fn(a)) cons(a, b) else b }, stream)
  }


  def append[A](element: A, stream: Stream[A]) : Stream[A] = {
    foldRight[A, Stream[A]](Empty)((a, b) => cons(a,b), cons(element, stream))
  }

}


def generateRandomInt(): Int = {
  math.random * 100 toInt
}




val myStream = Stream.generate(10, generateRandomInt)
for (x <-  Stream.convertStreamTolist(myStream)) println(x)

def exists[A](fn: (A) => Boolean, stream: Stream[A]) : Boolean = {
  Stream.foldRight[A, Boolean](false)((a, b) => fn(a) || b, stream)
}

def forAll[A](fn: A => Boolean, stream: Stream[A]) : Boolean = {
  Stream.foldRight[A, Boolean](true)((a, b) => fn(a) && b, stream)
}

def takeWhile[A](fn: A => Boolean)(stream: Stream[A]) : Stream[A] = {

  def myFunc(a: A, stream: => Stream[A]) : Stream[A] = {
    if (fn(a)) Stream.cons[A](a, stream) else Empty
  }

    Stream.foldRight[A, Stream[A]](Empty)(myFunc, stream)
}

import Stream._

convertStreamTolist(filter((x:Int) => x%2 == 0)(map((x: Int) => x+10)(myStream)))

//for (x <-  Stream.convertStreamTolist(Stream.append(100, myStream))) println(x)


//Stream.headOptionWithFold(myStream)

//exists((x: Int) => {
//  println("evaluating"); x > 25
//}, myStream)

//forAll((x: Int) => {
//  println("evaluating"); x > 25
//}, myStream)

//Stream.convertStreamTolist(takeWhile((x: Int) => {println("evaluating"); x > 25})(myStream))






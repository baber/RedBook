

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

  def dropWhile[A](fn: A => Boolean, stream: Stream[A]): Stream[A] = {
    stream match {
      case Cons(h, t) if fn(h()) => dropWhile(fn, t())
      case _ => stream
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


  def headOption[A](stream: Stream[A]): Option[A] = {
    stream match {
      case Empty => None
      case Cons(h, t) => Some(h())
    }
  }

  def headOptionWithFold[A](stream: Stream[A]): Option[A] = {
    Stream.foldRight[A, Option[A]](None)((a, b) => Some(a), stream)
  }

  def map[A, B](fn: A => B)(stream: Stream[A]): Stream[B] = {
    foldRight[A, Stream[B]](Empty)((a, b) => Stream.cons(fn(a), b), stream)
  }

  def filter[A](fn: A => Boolean)(stream: Stream[A]): Stream[A] = {
    foldRight[A, Stream[A]](Empty)((a, b) => {
      if (fn(a)) cons(a, b) else b
    }, stream)
  }


  def append[A](element: A, stream: Stream[A]): Stream[A] = {
    foldRight[A, Stream[A]](Empty)((a, b) => cons(a, b), cons(element, stream))
  }

}


def generateRandomInt(): Int = {
  math.random * 100 toInt
}




val myStream = Stream.generate(10, generateRandomInt)
for (x <- Stream.convertStreamTolist(myStream)) println(x)

def exists[A](fn: (A) => Boolean, stream: Stream[A]): Boolean = {
  Stream.foldRight[A, Boolean](false)((a, b) => fn(a) || b, stream)
}

def forAll[A](fn: A => Boolean, stream: Stream[A]): Boolean = {
  Stream.foldRight[A, Boolean](true)((a, b) => fn(a) && b, stream)
}

def takeWhile[A](fn: A => Boolean)(stream: Stream[A]): Stream[A] = {

  def myFunc(a: A, stream: => Stream[A]): Stream[A] = {
    if (fn(a)) Stream.cons[A](a, stream) else Empty
  }

  Stream.foldRight[A, Stream[A]](Empty)(myFunc, stream)
}

import Stream._

convertStreamTolist(filter((x: Int) => x % 2 == 0)(map((x: Int) => x + 10)(myStream)))

def findUsingFilter[A](fn: A => Boolean)(stream: Stream[A]): Option[A] = {
  import Stream._

  headOption(filter(fn)(stream))
}


//findUsingFilter[Int](_ > 90)(myStream)


val ones: Stream[Int] = Stream.cons(1, ones)
def constantValues[A](x: A): Stream[A] = Stream.cons(x, constantValues(x))


def myFunc(x: Int, y: Int): Stream[Int] = {
  Stream.cons(x, Stream.cons(y, myFunc(x + y, y + x + y)))
}

def fibonacciSeq = myFunc(0, 1)

Stream.take(10, fibonacciSeq)


def unfold[A, S](x: S)(f: S => Option[(A, S)]): Stream[A] = {
  f(x) match {
    case None => Empty
    case Some((value, state)) => Stream.cons(value, unfold(state)(f))
  }
}

def fibonacciWithUnfold = {
  unfold[Int, (Int, Int)]((0, 1))((x: (Int, Int)) => Some((x._1 + x._2, (x._2, x._1 + x._2))))
}

def monotonicIncreaseWithUnfold = {
  unfold[Int, Int](0)((x: Int) => Some(x + 1, x + 1))
}

def mapWithUnfold[A, B](f: A => B)(stream: Stream[A]): Stream[B] = {

  def myFunc(xs: Stream[A]): Option[(B, Stream[A])] = {
    xs match {
      case Cons(head, tail) => Some(f(head()), tail())
      case Empty => None
    }
  }

  unfold[B, Stream[A]](stream)(myFunc)
}



def zipAllWithUnfold[A, B](first: Stream[A], second: Stream[B]): Stream[(Option[A], Option[B])] = {

  def myFunc(x: (Stream[A], Stream[B])): Option[((Option[A], Option[B]), (Stream[A], Stream[B]))] = {
    x match {
      case (Cons(head1, tail1), Cons(head2, tail2)) => Some((Some(head1()), Some(head2())), (tail1(), tail2()))
      case (Cons(head1, tail1), Empty) => Some((Some(head1()), None), (tail1(), Empty))
      case (Empty, Cons(head2, tail2)) => Some((None, Some(head2())), (Empty, tail2()))
      case _ => None
    }
  }

  unfold((first, second))(myFunc)

}

//Stream.take(10, zipAllWithUnfold(Stream.generate(5, generateRandomInt), monotonicIncreaseWithUnfold))

def longestMatch[A](stream: Stream[A], subsequence: Stream[A]): Stream[A] = {
  (stream, subsequence) match {
    case (Cons(head1, tail1), Cons(head2, tail2)) if head1() == head2() => Stream.cons[A](head1(), longestMatch(tail1(), tail2()))
    case _ => Empty
  }
}

def length[A](stream: Stream[A]): Long = {
  Stream.foldRight[A, Long](0)((a, b) => b + 1, stream)
}

def containsSubsequence[A](stream: Stream[A], subsequence: Stream[A]): Boolean = {
  stream match {
    case (Cons(h, t)) => {
      if (length(longestMatch(stream, subsequence)) == length(subsequence)) true
      else containsSubsequence(Stream.dropWhile((x: A) => x != Stream.headOption(subsequence).get, t()), subsequence)
    }
    case _ => false
  }

}

//val result = containsSubsequence(fibonacciSeq, Stream.cons(5, Stream.cons(8, Empty)))


def startsWith[A](stream: Stream[A], sequence: Stream[A]): Boolean = {
  val matchingPrefix = (stream, sequence) match {
    case (Cons(head1, tail1), Cons(head2, tail2)) if head1() == head2() => Stream.cons[A](head1(), longestMatch(tail1(), tail2()))
    case _ => Empty
  }

  length(matchingPrefix) == length(sequence)
}

//startsWith(fibonacciSeq, Stream.cons(0, Stream.cons(1, Stream.cons(1, Stream.cons(2, Stream.cons(3, Empty))))))

def tails[A](stream: Stream[A]): (Stream[Stream[A]]) = {

  def myFunc[A](xs: Stream[A]): Option[(Stream[A], Stream[A])] = {
    xs match {
      case (Cons(h, t)) => Some(Cons(h, t), t())
      case _ => None
    }
  }

  unfold(stream)(myFunc)

}


def hasSubsequence[A](stream: Stream[A], sequence: Stream[A]) : Boolean = {
  exists((x: Stream[A]) => startsWith(x, sequence), tails(stream))
}

def scanLeft[A,B](init: B)(stream: Stream[A])(fn: (B, A) => B) : Stream[B] = {

  stream match {
    case Empty => Stream.cons(init, Empty)
    case Cons(h,t) => {
      val myRes = fn(init, h())
      Cons(() => init, () =>scanLeft(myRes)(t())(fn))
    }
  }
}



def scanRightWithUnfold[A,B](init: B)(stream: Stream[A])(fn: (B, A) => B) : Stream[B] = {

  def myFunc(x: (B, Stream[A]))  = {
    x match {
      case (_, Empty) => None
      case (value, Cons(h, t)) => {
        val newValue = fn(value, h())
        Some(newValue, (newValue, t()))
      }
    }
  }

  unfold((init, stream))(myFunc)

}


def foldRight[A,B](init: B)(stream: Stream[A])(fn: (B, A) => B) : B = {
  stream match {
    case Empty => init
    case Cons(h,t) => fn(foldRight(init)(t())(fn),  h())
  }
}




def scanRight[A,B](init: B)(stream: Stream[A])(fn: (B, A) => B) : Stream[B] = {

  def unfoldWithIntermediates(fn: (B, A) => B)(tuple: (B, Stream[B]), value: A) : (B, Stream[B]) = {
    val x = fn(tuple._1, value)
    (x, Cons(() => x, () => tuple._2))
  }

  def scanWithIntermediates(init: (B, Stream[B]))(stream: Stream[A])(fn: ((B, Stream[B]), A) => (B, Stream[B])) : (B, Stream[B]) = {
    stream match {
      case Empty => init
      case Cons(h,t) => fn(scanWithIntermediates(init)(t())(fn),  h())
    }
  }

  def fx = unfoldWithIntermediates(fn)_
  scanWithIntermediates((init, Stream.empty[B]))(stream)(fx)._2
}

val aStream = cons(1, cons(2, cons(3, Empty)))
val result = scanLeft(0)(aStream)(_ + _)
Stream.convertStreamTolist(result)







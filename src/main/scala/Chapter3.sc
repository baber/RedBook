import scala.annotation.tailrec

//def computeLengthViaFoldRight[A](list: List[A]) = {
//  list.foldRight(0)((_, b) => b+1)
//}
//
//val myList = List("a", "b", "c", "d")
//
//computeLengthViaFoldRight(myList)

// --------------------------------------------------------------

//def foldRight[A,B](xs: List[A], z: B)(fn: (A,B) => B): B = {
//  xs match {
//    case (Nil | List()) => z
//    case _ => fn(xs.head, foldRight(xs.tail, z)(fn))
//  }
//
//}

//foldRight(myList, 0)((x: Int, y: Int) => x + y)

// -----------------------------------------------------------------


//def foldLeft[A,B](xs: List[A], z: B)(fn: (B,A) => B) : B = {
//  xs match {
//    case (Nil | List()) => z
//    case _ => foldLeft(xs.tail, fn(z, xs.head))(fn)
//  }
//}
//
//def myList = (1 to 10).toList
//
//def reverse[A](list: List[A]) = {
//  foldLeft(list, List[A]())((xs,x) => x :: xs)
//}
//
//reverse(myList)

// --------------------------------------------------------------

//def foldLeft[A, B](xs: List[A], z: B)(fn: (B, A) => B): B = {
//  xs match {
//    case (Nil | List()) => z
//    case _ => foldLeft(xs.tail, fn(z, xs.head))(fn)
//  }
//}
//
//
//def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B = {
//  as match {
//    case Nil => z
//    case x :: xs => f(x, foldRight(xs, z)(f))
//  }
//}
//
//def foldLeftUsingFoldRight[A, B](as: List[A], z: B)(f: (B, A) => B): B = {
//  foldRight(as.reverse, z)((a: A, b: B) => f(b, a))
//}
//
//def foldRightUsingFoldLeft[A, B](as: List[A], z: B)(f: (A, B) => B): B = {
//  foldLeft(as.reverse, z)((b: B, a: A) => f(a, b))
//}
//
//
//val stringSeq = Seq("a", "b", "c", "d").toList
//foldLeft(stringSeq, "-")(_ + _)
//foldLeftUsingFoldRight(stringSeq, "-")(_ + _)
//
//foldRight(stringSeq, "-")(_ + _)
//foldRightUsingFoldLeft(stringSeq, "-")(_ + _)

// ------------------------------------------------------------------------

//def concatTwoLists[A](xs: List[A], ys: List[A]) = {
//  foldLeft(xs.reverse, ys)((list, a) => a :: list)
//}
//
//def concatenateLists[A](list: List[List[A]]) : List[A] = {
//  foldLeft(list, List[A]())(concatTwoLists)
//}
//
//concatenateLists(List(List("a", "b"), List("c"), List("d", "e", "f")))

// ------------------------------------------------------------------------


//def map[A, B](list: List[A])(f: A => B): List[B] = {
//
//  @tailrec
//  def innerMap[A, B](list: List[A], acc: List[B])(f: A => B): List[B] = {
//    list match {
//      case List() => acc
//      case _ => innerMap(list.tail, f(list.head) :: acc)(f)
//    }
//  }
//
//  innerMap(list.reverse, List())(f)
//}
//
//val myList = List(1, 2, 3, 4, 5)
//
//map(myList)(x => x + x)

// --------------------------------------------------------------------------


//def filter[A](list: List[A])(f: A => Boolean): List[A] = {
//
//  @tailrec
//  def innerFilter[A](list: List[A], acc: List[A])(f: A => Boolean): List[A] = {
//    list match {
//      case List() => acc
//      case x :: xs => if (f(x)) innerFilter(xs, x :: acc)(f) else innerFilter(xs, acc)(f)
//    }
//  }
//
//  innerFilter(list.reverse, List())(f)
//}
//
//val aList = List(1, 2, 11, 4, 5, 6)
//filter(aList)(_ > 4)

def flatMap[A, B](list: List[A])(f: A => List[B]) : List[B] = {
  @tailrec
  def innerFlatMap[A,B](list: List[A], acc: List[B])(f: A => List[B]) : List[B] = {
    list match {
      case List() => acc
      case x::xs => innerFlatMap(xs, acc ++ f(x))(f)
    }
  }

  innerFlatMap(list, List())(f)
}

flatMap(List(1,2,3))(a => List(a,a))










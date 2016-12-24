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

//def flatMap[A, B](list: List[A])(f: A => List[B]) : List[B] = {
//  @tailrec
//  def innerFlatMap[A,B](list: List[A], acc: List[B])(f: A => List[B]) : List[B] = {
//    list match {
//      case List() => acc
//      case x::xs => innerFlatMap(xs, acc ++ f(x))(f)
//    }
//  }
//
//  innerFlatMap(list, List())(f)
//}
//
//flatMap(List(1,2,3))(a => List(a,a))
//


//val myPFunc: PartialFunction[Option[Int], String] = {
//    case x: Some[Int] => "My integer is " + x.get
//}
//
////val partialFunction = PartialFunction[Option[Int], String](myFunc)
//
////partialFunction(None)
//
//val myList = List(Some(1), None, Some(3))
//myList.collect(myPFunc)


// ------------------------------------------------------------------------------------

//def mapIfIsDefined[A, B](list: List[A], f: PartialFunction[A,B]) = {
//
//  def inner[A, B](list: List[A], acc: List[B], f: PartialFunction[A,B]): List[B] = {
//    list match {
//      case List() => acc
//      case x :: xs => {
//        if (f.isDefinedAt(x))
//          inner(xs, f(x) :: acc, f)
//        else
//          inner(xs, acc, f)
//      }
//    }
//  }
//
//  inner(list.reverse, List(), f)
//}
//
//val fullyDefined: PartialFunction[Int, Int] = { case x if x > 5 => x * x }
//
//mapIfIsDefined(List(11, 2, 3, 5, 9, 13), fullyDefined)

// ----------------------------------------------------------------------------------
//def zipWith[A](xs: List[A], ys: List[A])(f: (A,A) => A) : List[A] = {
//
//  def myFunc[A](xs: List[A], ys: List[A], acc: List[A])(f: (A,A) => A) : List[A] = {
//    (xs,ys) match {
//      case ((List(), _) | (_, List())) => acc
//      case _ => myFunc(xs.tail, ys.tail, f(xs.head, ys.head) :: acc)(f)
//    }
//  }
//
//  myFunc(xs,ys, List())(f).reverse
//}
//
//
//zipWith(List(1), List(4,5))(_*_)

// --------------------------------------------------------------------------------------


// --------------------------------------------------------------------------------------
//def matchFromHead[A](seq: List[A], subSeq: List[A]) : List[A] = {
//  (seq, subSeq) match {
//    case (Nil, _) | (_, Nil) => Nil
//    case (x::xs, y::ys) => if (x == y) x :: matchFromHead(xs, ys) else Nil
//  }
//}
//
//
//def containsSequence[A](sequence: List[A], subsequence: List[A]) : Boolean = {
//  println(sequence)
//  sequence match {
//    case List() => false
//    case _ => {
//      if (matchFromHead(sequence, subsequence).size == subsequence.size)
//        true
//      else
//        containsSequence(sequence.tail.dropWhile(_ != subsequence.head), subsequence)
//
//    }
//  }
//}
//
//
//containsSequence(List(1,2,3,4,5,6,9,4,5,7,10), List(4,5,7))

// -----------------------------------------------------------------------------------------

//sealed trait Tree[+A]
//case class Branch[A](value: A, left: Option[Tree[A]] = None, right: Option[Tree[A]] = None) extends Tree[A]


//def generateTree[A](depth: Int, f: () => A) : Tree[A] = {
//  depth match {
//    case 1 => Branch(f(), None, None)
//    case x if x > 1 => Branch(f(), Some(generateTree(depth-1, f)), Some(generateTree(depth-1, f)))
//  }
//}


def randomValueFunction() = {
  new java.util.Random().nextInt(1000)
}

//def map[A,B](tree: Tree[A])(f : A => B) : Tree[B] = {
//  tree match {
//    case Branch(value, None, None) => Branch(f(value), None, None)
//    case Branch(value, Some(left), None) => Branch(f(value), Some(map(left)(f)), None)
//    case Branch(value, None, Some(right)) => Branch(f(value), None, Some(map(right)(f)))
//    case Branch(value, Some(left), Some(right)) => Branch(f(value), Some(map(left)(f)), Some(map(right)(f)))
//  }
//}

import com.bk.scala.redbook.datastructures._

def fold[A, B](tree: BinaryTree[A], acc: B)(f: (B,A) => B) : B = {
  tree match {
    case BinaryTree(Some(value), None, None) => f(acc, value)
    case BinaryTree(Some(value), Some(left), None) => fold(left, f(acc, value))(f)
    case BinaryTree(Some(value), None, Some(right)) => fold(right, f(acc, value))(f)
    case BinaryTree(Some(value), Some(left), Some(right)) => fold(right, fold(left, f(acc, value))(f))(f)
  }
}

import com.bk.scala.redbook.datastructures.BinaryTree.::

def map[A,B](f: A => B, tree: BinaryTree[A]) : BinaryTree[B] = {
  fold(tree, BinaryTree[B](None, None, None))((a, b) => ::(a, f(b)))
}


val emptyTree = BinaryTree[Int](None, None, None)
val myTree: BinaryTree[Int] = ::(::(::(::(::(emptyTree, 1), 5), 10), 78), 9)


val countNodesUsingFold = fold(myTree, 0)((a,b) => a + 1)
val maxValue = fold(myTree, 0)((a,b) => a.max(b))
val treeStructure = fold(myTree, "")((a,b) => b + ", " + a)

val mappedTree = map( (a: Int) => a*2, myTree)
fold(mappedTree, "")((a,b) => b + ", " + a)





//def generateUTRs() = {
//  (1 to 500000 toStream) map { "UTR" + _}
//}
//
//import java.io._
//val pw = new PrintWriter(new File("/Users/baberkhalil/tmp/ats_test/utr.lst" ))
//
//for (utr <- generateUTRs()) {
//  pw.write(s"${utr}\n")
//}
//
//pw.close

























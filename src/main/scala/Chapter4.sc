

trait Option[+A] {
  def map[B](fn: A => B): Option[B]

  def flatMap[B](fn: A => Option[B]): Option[B]

  def getOrElse[B >: A](alt: => B): B

  def orElse[B >: A](alt: => Option[B]): Option[B]

  def filter(fn: A => Boolean): Option[A]
}

case object None extends Option[Nothing] {
  def map[B](fn: (Nothing) => B): Option[B] = None

  def flatMap[B](fn: (Nothing) => Option[B]): Option[B] = None

  def getOrElse[B >: Nothing](alt: => B): B = alt

  def orElse[B >: Nothing](alt: => Option[B]): Option[B] = alt

  def filter(fn: (Nothing) => Boolean): Option[Nothing] = None
}


case class Some[+A](v: A) extends Option[A] {
  def map[B](fn: (A) => B): Option[B] = Some(fn(v))

  def flatMap[B](fn: A => Option[B]): Option[B] = {
    fn(v)
  }

  def getOrElse[B >: A](alt: => B): B = v

  def orElse[B >: A](alt: => Option[B]): Option[B] = Some(v)

  def filter(fn: (A) => Boolean): Option[A] = if (fn(v)) Some(v) else None

  def unapply() : A =  v

}


def mean(xs: Seq[Double]): Option[Double] = {
  xs match {
    case Nil | List() => None
    case _ => Some(xs.foldLeft(0.0)(_ + _) / xs.length)
  }
}


// math.pow(x-m, 2)  m = mean
def variance(xs: Seq[Double]): Option[Double] = {
  mean(xs) flatMap { m =>
    mean(xs.map(x => math.pow(x - m, 2)))
  }
}

mean(List(1, 2.5, 3.5))
variance(List())


def traverse[A, B](xs: List[A], f: A => Option[B]): Option[List[B]] = {

  def innerTraverse[A, B](xs: Seq[A], f: A => Option[B], acc: Option[List[B]]): Option[List[B]] = {

    xs match {
      case List() => acc
      case y :: ys => {
        f(y) match {
          case Some(value) => innerTraverse(ys, f, Some(value :: acc.getOrElse(List())))
          case None => None
        }
      }
    }
  }

  innerTraverse(xs.reverse, f, None)

}

def fn(x: Int) = {
  x match {
    case x:Int if x <= 3 => Some(x * 2)
    case _ => None
  }
}

traverse(List(1,2,3,4), fn)







package com.bk.scala.redbook.datastructures


sealed trait Tree[+A]
case object Nil extends Tree[Nothing]

class BinaryTree[+A](val value: Option[A], val left: Option[BinaryTree[A]] = None, val right: Option[BinaryTree[A]] = None) extends Tree[A]

object BinaryTree {


  def ::[A](tree: Tree[A], x: A) : BinaryTree[A] = {
    tree match {
      case BinaryTree(None, None, None) => BinaryTree(Some(x), None, None)
      case BinaryTree(Some(v), None, None) => BinaryTree(Some(v), Some(BinaryTree(Some(x), None, None)), None)
      case BinaryTree(Some(v), Some(l), None) => BinaryTree(Some(v), Some(l), Some(BinaryTree(Some(x), None, None)))
      case BinaryTree(Some(v), None, Some(r)) => BinaryTree(Some(v), Some(BinaryTree(Some(x), None, None)), Some(r))
      case BinaryTree(Some(v), Some(l), Some(r)) => BinaryTree(Some(v), Some(::(l, x)), Some(r))
    }
  }

  def unapply[A](tree: BinaryTree[A]) = {
    Some((tree.value, tree.left, tree.right))
  }


  def apply[A](x: Option[A], left: Option[BinaryTree[A]], right: Option[BinaryTree[A]]) = {
    new BinaryTree(x, left, right)
  }

}





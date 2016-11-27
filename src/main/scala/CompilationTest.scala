object CompilationTest {

  val myFunc: PartialFunction[Int, Int] = { case x if x > 5 => x * x }

}


trait myPartialFunc extends PartialFunction[Int, String] {

  def mpList : List[String]

  override def isDefinedAt(x: Int) = x > 5
  def apply(v1: Int) = s"Value is $v1"
}

val myList = List(3,9,7,1)
myList.collect(new myPartialFunc {
  def mpList: List[String] = ???
})



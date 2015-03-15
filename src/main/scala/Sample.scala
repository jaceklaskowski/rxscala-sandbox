import rx.lang.scala.Observable

object Sample {
  def ints: Observable[Int] = Observable.from(0 to 10)
}

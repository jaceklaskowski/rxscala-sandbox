import java.util.concurrent.TimeUnit

import rx.Scheduler
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.{Observable, Observer, Subscription}
import rx.schedulers.Schedulers

object ObservableMain extends App {
  def dataSync(): Observable[String] =
    Observable.create { (o: Observer[String]) =>
      println(s"Hello, $o")
      println("Sleeping for a sec before emitting messages using onNext...")
      TimeUnit.SECONDS.sleep(1)
      o.onNext("hello")
      o.onNext(", ")
      o.onNext("world")
      println("Sleeping for a sec before completing the stream...")
      TimeUnit.SECONDS.sleep(1)
      o.onCompleted()
      Subscription()
    }

  def deferObservable(): Observable[String] =
    Observable.just("hello", "world", "from", "jacek")

  val stringObservable: Observable[String] = dataSync()

  val stringObserver = new Observer[String] {
    def log(msg: String) = println(s">>> Observer: $msg")

    var received = ""

    override def onCompleted(): Unit = {
      log("onCompleted called")
      log(s"Entire message: $received")
    }

    override def onError(error: Throwable): Unit = {
      log("onError called and ignored")
    }

    override def onNext(value: String): Unit = {
      log(s"onNext called with value: $value")
      received += value
    }

    override def toString: String = "stringObserver"
  }
  stringObservable.subscribe(stringObserver)
  val f: ((String, String)) => Unit = {
    case (s1, s2) =>
      println(s"Received: $s1 and $s2")
  }
  stringObservable.zip(deferObservable()).subscribe(f)
  println(s"Current thread: ${Thread.currentThread().getName}")
  stringObservable.+:("xxx").subscribeOn(IOScheduler()).foreach(e =>
    println(s"Received: $e [${Thread.currentThread().getName}}]"))
  TimeUnit.SECONDS.sleep(2)
}
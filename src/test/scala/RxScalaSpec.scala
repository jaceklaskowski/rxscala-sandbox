import java.util.concurrent.TimeUnit

import org.specs2._
import rx.Scheduler
import rx.lang.scala.{Subscription, Subscriber, Scheduler, Observable}
import rx.lang.scala.schedulers.{IOScheduler, NewThreadScheduler, ComputationScheduler}

import scala.concurrent.duration._
import scala.io.Source

class RxScalaSpec extends Specification { def is = s2"""

  This is a specification for the 'Hello world' string

  The 'Hello world' string should
    end with 'world'  $e1
    read a file in chunks $e2
    subscribe to ints $e3
    print out thread names for `Observable.just` and `Observable.create` $e4 ${tag("e4")}
  """

  // Execute with `~testOnly RxScalaSpec -- include e4`
  def e4 = {
    println(s"[Example] on ${Thread.currentThread().getName}")

    def getDataFromVeeerySlowDatabase() = {
      TimeUnit.SECONDS.sleep(2)
      5
    }

    Observable.just {
      println(s"[Observable.just] on ${Thread.currentThread().getName}")
      getDataFromVeeerySlowDatabase()
    }.subscribeOn(NewThreadScheduler())
    .foreach { n =>
      println(s"[Observable.just] (foreach) on ${Thread.currentThread().getName}")
    }

    Observable.create[Int] { observer =>
      println(s"[Observable.create] on ${Thread.currentThread().getName}")
      observer.onNext(getDataFromVeeerySlowDatabase())
      observer.onCompleted()
      Subscription()
    }.subscribeOn(NewThreadScheduler())
    .foreach { n =>
      println(s"[Observable.create] (foreach) on ${Thread.currentThread().getName}")
    }
    success
  }

  def e1 = {
    println(s"Starting on threadId: ${Thread.currentThread().getName}")
    Observable.from(0 to 3)
      .doOnEach(_ => println(s"doOnEach: ${Thread.currentThread().getName}"))
      .observeOn(NewThreadScheduler())
      .subscribe(_ => println(s"subscribe: ${Thread.currentThread().getName}"))
    ok
  }

  def e2 = {
    val strings = Observable.from {
      println(s"Observable.from executed on [${Thread.currentThread().getName}]")
      println(s"The file is OPEN here.")
      "This is a veeery long file".split(" ")
    }
      .subscribeOn(IOScheduler())
      .subscribe { s =>
        println(s"Saving to a database: [$s] on [${Thread.currentThread().getName}]")
        TimeUnit.SECONDS.sleep(1)
      }

    Observable.interval(100 millis)
    pending
  }

  def e3 = {
    Sample.ints.filter(_ % 2 == 0).subscribe(new Subscriber[Int] {
      override def onStart(): Unit = println("+++ onStart")
      override def onNext(n: Int): Unit = println(s"+++ onNext: [$n]")
      override def onError(e: Throwable): Unit = println(s"+++ onError: [$e]")
      override def onCompleted(): Unit = println(s"+++ onCompleted")
    })
    pending
  }
}
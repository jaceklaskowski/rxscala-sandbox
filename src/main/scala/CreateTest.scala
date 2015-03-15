import rx.lang.scala._

object CreateTest extends App {

  def trace(msg: String) = println(s"[${Thread.currentThread.getName}] $msg")

  val f = (o: Observer[String]) => {
    trace(s"Observer $o has subscribed...emitting events")
    " ala ma   kota".trim.split("\\s+").foreach { s =>
      o.onNext(s)
    }
    o.onCompleted()
    Subscription()
  }

  trace("Let's the Rx game begin!")

  def onNext(s: String) = trace(s)
  def onError(t: Throwable) = trace(t.toString)
  def onCompleted() = trace("Completed")

  // We might not be able to modify subscriber (3rd party library)
  // It has to be def since it stops processing right after onCompleted/onError's called
  def createSubscriber = Subscriber[String](
    (onNext _), (onError _), (onCompleted _)
  )

  // We might not be able to modify observable (emits events) - 3rd party library
  val myObservable = Observable.create(f)

  // map to the rescue - intermediary step
  myObservable
    .map(_.toUpperCase)
    .subscribe(createSubscriber)

  val subscription = Observable.just((0 to 5))
    .flatMap(ns => Observable.from(ns))
    .map(_.toString)
    .subscribe(createSubscriber)
  
  trace(subscription.toString)

  trace("Observer subscribed")
}
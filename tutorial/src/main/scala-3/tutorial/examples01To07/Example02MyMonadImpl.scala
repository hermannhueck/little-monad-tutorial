package tutorial.examples01To07

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

import tutorial.libMyCats.*

import util.*
import scala.util.chaining.*

@main def Example02(): Unit =

  line().green pipe println

  println("----- List:")

  val l1 = List(1, 2, 3)
  val l2 = List(10, 20, 30, 40)

  val lResult = compute(l1, l2)
  println(lResult)

  val lResult2 = compute2(l1, l2)
  println(lResult2)

  val l3: List[List[Int]] = l1 map { i =>
    l2.map(_ + i)
  }

  println(l3)
  val lResult3 = l3.flatten
  println(lResult3)

  println("----- Option:")

  val o1 = Option(1)
  val o2 = Option(10)

  val oResult = compute(o1, o2)
  println(oResult)

  println("----- Future:")

  // import ExecutionContext.Implicits.{given ExecutionContext}
  given ExecutionContext = ExecutionContext.global

  val f1 = Future(1)
  val f2 = Future(10)

  val fResult = compute(f1, f2)
  val res     = Await.result(fResult, 3.seconds) // BLOCKING !!!
  println(res)

  fResult onComplete {
    case Failure(exception) => println(exception.getMessage())
    case Success(value)     => println(value)
  }

  fResult onComplete (
      tryy =>
        println(
          tryy.fold(
            throwable => throwable.getMessage,
            value => value.toString
          )
        )
    )

  Thread.sleep(500L)

  line().green pipe println

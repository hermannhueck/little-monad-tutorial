package tutorial.examples

import scala.util.chaining._
import util._
import scala.language.implicitConversions

import tutorial.libMyCats._

@main def Example04: Unit = {

  lineStart() pipe println

  val e1 = Right(13).withLeft[String]
  val e2 = Right(21).withLeft[String]

  val eResult = compute(e1, e2)
  println(eResult)

  "--- Either#flatten:" pipe println
  Right(Right(42)).flatten pipe println
  Right(Left("error")).flatten pipe println
  Left(Left("error")).flatten pipe println
  Left("error").flatten pipe println

  "--- Either#map:" pipe println
  Right(21).map(_ * 2) pipe println
  Option.empty[Int].map(_ * 2) pipe println
  Left("error").withRight[Int].map(_ * 2) pipe println
  Left("error").withRight[Int].swap.map(_.toUpperCase).swap pipe println

  def [L, L2, R](either: Either[L, R]) leftMap (f: L => L2): Either[L2, R] =
      either.swap.map(f).swap

  Left("error").withRight[Int].leftMap(_.toUpperCase) pipe println

  val either1: Either[String, Int] = Left("error").withRight[Int]
  val either2: Either[String, Int] = Right(42).withLeft[String]

  val result: Either[String, (Int, Int)] =
    for {
      val1 <- either1
      val2 <- either2
    } yield (val1, val2)

  result pipe println

  lineEnd() pipe println
}

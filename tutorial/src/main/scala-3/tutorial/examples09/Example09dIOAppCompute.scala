package tutorial.examples09

import scala.util.chaining.*
import util.*

import tutorial.libMyCats.*

@main def IOAppCompute: Unit = {

  line().green pipe println

  val io1 = IO.pure(40)
  val io2 = IO pure 2

  val io3: IO[(Int, Int)] =
    for
      i1 <- io1
      i2 <- io2
    yield (i1, i2)
  io3.unsafeRun() pipe println

  val ioCompute: IO[(Int, Int)] = compute(io1, io2)
  val pair: (Int, Int)          = ioCompute.unsafeRun()
  pair pipe println

  // eager and lazy

  val eager: IO[Int] = IO pure {
    "eager computing ... " pipe println
    42
  }
  // eager.unsafeRun()

  val lazyy: IO[Int] = IO eval {
    "lazy computing ... " pipe println
    41
  }
  // lazyy.unsafeRun()

  line().green pipe println
}

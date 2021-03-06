# Chapter 04

# Either Monad

We implemented a Monad instance for the effect types
_List_, _Option_, _Future_ and _Id_. These type constructors
had one thing in common: They take only one type parameter.
And they are a good fit for the Monad trait. The Monad trait
requires a type constructor with exactly one type parameter.

```scala
trait Monad[F[_]] {
  // ...
}
```

_Either_ has two type parameters ... which makes
the implementation of the _Either_ Monad a bit more
challenging.

First we need to consider the fact that _map_ and
_flatMap_ (and also _flatten_) only apply to the right
most type parameter of a parameterized type. That means
for _Either_: _map_ applied to a _Left_ leaves the
_Either_ unchanged. Mapping over a _Right_ changes the
value encapsulated in the _Right_. The same is true for
_flatMap_ and _flatten_.

We say that _Either_ like other effect types with more
than one type parameter are <u>_right biased_</u>.

To implement the _Either_ Monad we have to fix the left
type parameter to make it applicable to the Monad trait.

A first approach could look like this:

```scala
// fix left type param to a String
type ErrorOr[A] = Either[String, A]

val errorOrMonad: Monad[ErrorOr] = new Monad[ErrorOr] {
  // ...
}
```

This works, but the solution is not generic. We would
have to redefine the *Either* Monad for any other left
type.

Generic solution: We turn the _val_ into a _def_ and
parameterize it with the left type. The _*_ (or deprecated _?_)
is a place holder for the right type param, which we will fix
inside the implementation:

```scala
// fix left type param to a L
implicit def eitherMonad[L]: Monad[Either[L, *]] = new Monad[Either[L, *]] {
  // ...
}
```

With the left type param fixed to _L_ we have created a
type constructor that requires only one additional type
parameter which allows us to define an _Either_ Monad
instance.

The deprecated question mark syntax in _Monad[Either[L, ?]]_
or new star syntax in _Monad[Either[L, *]]_ is not
regular Scala syntax. In order for this to work we need to add
an extra compiler plugin to our _build.sbt_: _kind-projector_

```scala
addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),
```

Without using _kind-projector_ the type of the _Either_
Monad in Scala syntax would have looked like this:
```scala
Monad[({ type lambda[x] = Either[L, x] })#lambda]
```
instead of
```scala
Monad[Either[L, *]]
```

The same principle holds for the implementation of Monad
instances for any other type with more than one type
parameter. When implementing the Monad instances for
_Function1_ and _Reader_ in the subsequent chapters, we
will proceed in the same way.

The implementation of the _Either_ Monad is straightforward: _pure_
lifts a value into a _Right_ and _flatMap_ just
delegates to the _flatMap_ implementation of _Either_:

```scala mdoc:invisible
// mdoc:invisible
trait Monad[F[_]] {

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  final def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))
  final def flatten[A](fa: F[F[A]]): F[A] =
    flatMap(fa)(identity)
}
// mdoc:invisible
```

```scala mdoc
object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

  implicit def eitherMonad[L]: Monad[Either[L, *]] = new Monad[Either[L, *]] {
    def pure[A](a: A): Either[L, A] =
      Right(a)
    def flatMap[A, B](fa: Either[L, A])(f: A => Either[L, B]): Either[L, B] =
      fa flatMap f
  }
}
```

We define the _Either_ Monad again in implicit scope,
i.e. in the _Monad_ companion object, where it is found
by the compiler automatically without the need to import
it explicitly into the local scope of the call site.

Now we can pass instances of _Either[Int]_ to our generic
_compute_ method as we do in _Example04_.

```scala mdoc:invisible
// mdoc:invisible
implicit final class MonadSyntax[F[_]: Monad, A](private val fa: F[A]) {
  @inline def flatMap[B](f: A => F[B]): F[B] =
    Monad[F].flatMap(fa)(f)
  @inline def map[B](f: A => B): F[B] =
    Monad[F].map(fa)(f)
}

def compute[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
  for {
    a <- fa
    b <- fb
  } yield (a, b)
// mdoc:invisible
```

```scala mdoc
val e1 = Right(13).withLeft[String]
val e2 = Right(21).withLeft[String]

val eResult = compute(e1, e2) // Right((13, 21))
```

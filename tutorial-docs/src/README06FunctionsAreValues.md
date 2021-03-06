# Chapter 06

# Functions are Values

Functions are very often used like methods. They are
invoked like methods getting arguments and returning
values like methods do. And more confusing... when you
pass a method name where a function is expected, the
compiler in many cases automatically converts the method
name into a function. But...

FUNCTIONS ARE VALUES.

Unlike methods functions can be stored in a value and
they can be treated like _Int_'s and _String_'s. They
can be passed as arguments to methods or other functions,
they can be returned from methods or other functions
(Higher Order Functions).
And they can be stored in data structures like _List_,
_Option_, _Map_, or in case classes. Functions can be
manipulated like _Int_'s or _String_'s. And they can be
mapped or flatMapped over, if you define a Monad instance
for them. Unlike methods...

FUNCTIONS ARE DATA.

## List of Functions

In this chapter we want to get familiar with this basic
idea, pack a bunch of _Function1_ into a _List_ and then
map over that _List_ or fold the _List_. In the subsequent
chapter we will define a Monad instance for _Function1_
or stuff a _Function1_ into a case class (_Reader Monad_).

A _List_ of functions from _Int_ to _Int_:

```scala mdoc
val lf1: List[Int => Int] = List(_ + 1, _ + 2, _ + 3)
```

With _List#map_ we can apply these functions to a value
and get a _List_ of _Int_ values as a result:

```scala mdoc
lf1.map(f => f(10)) // List(11, 12, 13)
```

With _List#map_ we can also manipulate the functions
by composing them with another function.

```scala mdoc
val double: Int => Int = _ * 2
val lf2 = lf1 map (f => f andThen double)
lf2.map(f => f(10)) // List(22, 24, 26)
```

It is also possible to fold over the _List_ of
_Function1_, thus composing them all with _andThen_ to a
single function.

```scala mdoc
val fComposed: Int => Int =
  lf1.fold(identity[Int] _)((f, g) => f andThen g)
val result: Int = fComposed(10) // 16
```

## Eta expansion

Often a method name is expanded to a function, because
the method name is used where a function is expected.
(_map_ requires a function as parameter.) The example
below shows 4 variants of eta expansions, the first
is very explicit, the last one expands the method
name implicitly without further ceremony.

```scala mdoc

def times2(x: Int): Int = x * 2

// times2 is a method.
// We will use it where a function is expected.

// explicitly expanding times2 to a function
List(1, 2, 3) map { x => times2(x) }

// using _
List(1, 2, 3) map { times2(_) }

// using _ appended to the method name (no longer supported in Scala 3)
List(1, 2, 3) map { times2 _ }

// implicit expansion just using the method name
List(1, 2, 3) map times2
```

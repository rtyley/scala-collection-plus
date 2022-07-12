# scala-collection-plus

[![scala-collection-plus artifacts](https://index.scala-lang.org/rtyley/scala-collection-plus/scala-collection-plus/latest-by-scala-version.svg)](https://index.scala-lang.org/rtyley/scala-collection-plus/scala-collection-plus/)
[![CI](https://github.com/rtyley/scala-collection-plus/actions/workflows/ci.yml/badge.svg)](https://github.com/rtyley/scala-collection-plus/actions/workflows/ci.yml)

A few extras to handle Scala 2.13's deprecation of `.mapValues()`

I'm just a developer, standing in front of a Scala v2.13 `Map`, asking it to map its values.


```scala
import com.madgag.scala.collection.decorators._
```

## `mapV`

A concise replacement for `mapValues()`, that generates immutable Maps and
avoids the verbose `.view.mapValues(f).toMap` syntax that becomes necessary
in Scala 2.13:

```scala
val m = Map("foo" -> 5, "bar" -> 10)
// m: Map[String, Int] = Map("foo" -> 5, "bar" -> 10)
def f(i: Int) = i + 1

m.transform{case(_,v)=>f(v)}
// res0: Map[String, Int] = Map("foo" -> 6, "bar" -> 11)
m.view.mapValues(f).toMap
// res1: Map[String, Int] = Map("foo" -> 6, "bar" -> 11)
m.mapValues(f)
// res2: collection.MapView[String, Int] = MapView(("foo", 6), ("bar", 11))
m.mapV(f)                    
// res3: Map[String, Int] = Map("foo" -> 6, "bar" -> 11)
```

## `groupUp`

Scala 2.13 introduced 
[`groupMap` & `groupMapReduce`](https://docs.scala-lang.org/overviews/core/collections-migration-213.html#are-there-new-operations-on-collections)
(see original GitHub [Issue](https://github.com/scala/collection-strawman/issues/42) &
[PR](https://github.com/scala/collection-strawman/pull/253)) which potentially are another
way to avoid the verbosity of `mapValues` in Scala 2.13. In the codebase I'm working on, there
were unfortunately only a few examples where they were applicable.
 
There were more than twice as many opportunities to use a new `groupUp(k)(g)` method,
equivalent to `s.groupBy(k).view.mapValues(g).toMap` - the difference is that while
`groupMap` requires you to `map` over the individual elements in each collection that
forms a value in the `Map`, `groupUp` instead allows you to perform _any_ operation
(not just `map`) that you might want. In the codebase I'm working on, that turns out
to be more useful.

```scala
val s = Seq("foo", "foo", "bar", "bang")
// s: Seq[String] = List("foo", "foo", "bar", "bang")
def k(str: String) = str.length
def g(strs: Seq[String]) = strs.size

s.groupBy(k).view.mapValues(g).toMap // Scala 2.13 syntax
// res4: Map[Int, Int] = Map(3 -> 3, 4 -> 1) // Scala 2.13 syntax
s.groupBy(k).mapValues(g)            // deprecated in Scala 2.13, now returns MapView[K,B]
// res5: collection.MapView[Int, Int] = MapView((3, 3), (4, 1))            // deprecated in Scala 2.13, now returns MapView[K,B]
s.groupUp(k)(g)                      // provided by scala-213-collections-plus
// res6: Map[Int, Int] = Map(3 -> 3, 4 -> 1)
```

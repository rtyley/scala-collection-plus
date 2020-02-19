# scala-collection-plus

A few extras to handle Scala 2.13's deprecation of `.mapValues()`

I'm just a developer, standing in front of a Scala v2.13 `Map`, asking it to map it's values.


```scala mdoc
import com.madgag.scala.collection.decorators._
```

## `mapV`

A concise replacement for `mapValues()`, that generates immutable Maps and
avoids the verbose `.view.mapValues(f).toMap` syntax that becomes necessary
in Scala 2.13:

```scala mdoc
val m = Map("foo" -> 5, "bar" -> 10)
def f(i: Int) = i + 1

m.transform{case(_,v)=>f(v)}
m.view.mapValues(f).toMap
m.mapValues(f)
m.mapV(f)                    
```

## `groupUp`

Scala 2.13 introduced 
[`groupMap` & `groupMapReduce`](https://docs.scala-lang.org/overviews/core/collections-migration-213.html#are-there-new-operations-on-collections)
(see original GitHub [Issue](https://github.com/scala/collection-strawman/issues/42) &
[PR](https://github.com/scala/collection-strawman/pull/253)) which potentially are another
way to avoid the verbosity of Scala 2.13. In the codebase I'm working on, there were
unfortunately only a few examples where they were applicable, and more than twice as many
where a new `groupUp(k)(g)` method, equivalent to `s.groupBy(k).view.mapValues(g).toMap`,
would be useful - the difference is that `groupMap` requires you to `map` over individual
elements in each collection that forms a value in the `Map`, whereas `groupUp` allows you
to perform an operation **other** than `map`. In the codebase I'm working on, that turns
out to be much more useful.

```scala mdoc
val s = Seq("foo", "foo", "bar", "bang")
def k(str: String) = str.length
def g(strs: Seq[String]) = strs.size

s.groupBy(k).view.mapValues(g).toMap // Scala 2.13 syntax
s.groupBy(k).mapValues(g)            // deprecated in Scala 2.13, now returns MapView[K,B]
s.groupUp(k)(g)                      // provided by scala-213-collections-plus
```
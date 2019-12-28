package com.madgag.scala.collection.decorators

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.SortedSet

class IterableDecoratorTest extends FlatSpec with Matchers {

  val s = Seq("aa", "aaa", "aaaaa", "bbb", "bb")

  def check_groupUp_matchesOfficialImplementationFor[A,K,B](m: Iterable[A])(k: A => K)(f: Iterable[A] => B): Map[K,B] = {
    val distinctResultsForGroupingUpByDifferentMethods = Map[Map[K,B], String](
      m.groupBy(k).view.mapValues(f).toMap -> "groupBy & mapValues",
   // m.groupBy(k).mapValues(f) - deprecated in Scala 2.13, now returns MapView[K,B] rather than Map[K,B]
      m.groupUp(k)(f) -> "groupUp",
    )
    distinctResultsForGroupingUpByDifferentMethods should have size 1

    distinctResultsForGroupingUpByDifferentMethods.head._1
  }

  "groupUp" should "group up an iterable and transform the resulting values" in {
    s.groupUp(_.length)(_.size) shouldBe Map(2 -> 2, 3 -> 2, 5 -> 1)
  }

  it should "create results identical to those produced by the official Scala library implementations" in {
    val words = Seq("foo1", "foo2", "foo3", "bar1", "bar2", "baz1", "roberto")
    check_groupUp_matchesOfficialImplementationFor(words)(_.length)(_.map(_.dropRight(1))) shouldBe Map(
      4 -> Seq("foo","foo","foo","bar","bar", "baz"),
      7 -> Seq("robert")
    )

    check_groupUp_matchesOfficialImplementationFor(words.toSet)(_.length)(_.map(_.dropRight(1))) shouldBe Map(
      4 -> Set("foo","bar", "baz"),
      7 -> Set("robert")
    )

    check_groupUp_matchesOfficialImplementationFor(SortedSet(words: _*))(_.length)(_.map(_.dropRight(1))) shouldBe Map(
      4 -> SortedSet("foo","bar", "baz"),
      7 -> SortedSet("robert")
    )

    Map("foo" -> 10, "bar" -> 15, "bang" -> 37).groupUp(_._1.length)(_.values.sum) shouldBe Map(
      3 -> 25,
      4 -> 37
    )
  }
}

package com.madgag.scala.collection.decorators

import org.scalatest.Assertion
import org.scalatest.flatspec._
import org.scalatest.matchers._

import scala.collection.immutable.SortedMap

class MapDecoratorTest extends AnyFlatSpec with should.Matchers {

  val m = Map("foo" -> 5, "bar" -> 10)

  def check_mapV_matchesOfficialImplementationFor[K,V,U](m: Map[K,V])(f: V => U): Assertion = {
    val distinctResultsForMappingValuesByDifferentMethods = Set[Map[K,U]](
      m.transform{case(_,v)=>f(v)},
      m.view.mapValues(f).toMap,
      // m.mapValues(f) - deprecated in Scala 2.13, now returns MapView[K,U] rather than Map[K,U]
      m.mapV(f),
    )
    distinctResultsForMappingValuesByDifferentMethods should have size 1
  }

  "mapV" should "create sensible results for mapping the values of a map" in {
    m.mapV(_ + 1) shouldBe Map("foo" -> 6, "bar" -> 11)
  }

  it should "create results identical to those produced by the official Scala library implementations" in {
    check_mapV_matchesOfficialImplementationFor(Map("foo" -> 6, "bar" -> 11))(_ + 1)
    check_mapV_matchesOfficialImplementationFor(Map("foo" -> "apple", "bar" -> "tree"))(_.length)
    check_mapV_matchesOfficialImplementationFor(SortedMap("foo" -> "apple", "bar" -> "tree", "boo" -> "is"))(_.length)
  }
}

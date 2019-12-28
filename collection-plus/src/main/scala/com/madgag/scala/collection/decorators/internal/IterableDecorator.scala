package com.madgag.scala.collection.decorators.internal

import scala.collection.generic.IsIterable
import scala.collection.{MapOps, immutable, mutable}

class IterableDecorator[C, I <: IsIterable[C]](coll: C)(implicit val it: I) {
  private type MutableBuilder = mutable.Builder[it.A, C]

  private val iterOps = it(coll)

  private val newMutableBuilder: () => MutableBuilder = iterOps match {
    case mapOps: MapOps[_, _ , _ , C] =>
      () => mapOps.mapFactory.newBuilder.asInstanceOf[MutableBuilder]
    case _ =>
      () => iterOps.iterableFactory.newBuilder.asInstanceOf[MutableBuilder]
  }

  /**
   * Partitions this Iterable into a map using a discriminator function `key`.
   * Each group is transformed into a value of type `B` using the `value` function.
   *
   * It is equivalent to `groupBy(key).view.mapValues(f).toMap`, but more concise.
   *
   * {{{
   *   case class User(name: String, age: Int)
   *
   *   def nameFrequencyHistogram(users: Seq[User]): Map[String, Int] =
   *     users.groupUp(_.name)(_.size)
   * }}}
   *
   * The implementation is based on the implementation of groupMap
   * in the new Scala 2.13 collections library:
   *
   * https://github.com/scala/scala/blob/v2.13.1/src/library/scala/collection/Iterable.scala#L590-L602
   *
   * @param key the discriminator function
   * @param f the group transformation function
   * @tparam K the type of keys returned by the discriminator function
   * @tparam B the type returned by the transformation function
   */
  def groupUp[K, B](key: it.A => K)(f: C => B): immutable.Map[K, B] = {
    val m = mutable.Map.empty[K, MutableBuilder]
    for (elem <- iterOps) { m.getOrElseUpdate(key(elem), newMutableBuilder()) += elem }

    var res = immutable.Map.empty[K, B]
    for ((k,v) <- m) { res += k -> f(v.result()) }
    res
  }

}


package com.madgag.scala.collection.decorators.internal

import scala.collection.generic.IsMap

class MapDecorator[C, M <: IsMap[C]](coll: C)(implicit val map: M) {

  /** Transforms this map by applying a function to every retrieved value.
   *  @param  f   the function used to transform values of this map.
   *  @return an immutable map which has transformed every value to
   *          to `f(value)`. The resulting map is a concrete immutable map.
   */
  def mapV[U](f: map.V => U): Map[map.K, U] = map(coll).toMap.transform { case (_,v) => f(v) }

}
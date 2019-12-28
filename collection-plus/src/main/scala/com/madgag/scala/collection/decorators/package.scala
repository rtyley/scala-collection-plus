package com.madgag.scala.collection

import com.madgag.scala.collection.decorators.internal.{IterableDecorator, MapDecorator}

import scala.collection.generic.{IsIterable, IsMap}
import scala.language.implicitConversions

/*
 * If you would like some help understanding the code in this class,
 * you'll probably find both these helpful:
 * * https://docs.scala-lang.org/overviews/core/custom-collection-operations.html
 * * https://github.com/scala/scala-collection-compat/
 */
package object decorators {

  implicit def IterableDecorator[C](coll: C)(implicit it: IsIterable[C]): IterableDecorator[C, it.type] =
    new IterableDecorator(coll)(it)

  implicit def MapDecorator[C](coll: C)(implicit map: IsMap[C]): MapDecorator[C, map.type] =
    new MapDecorator(coll)(map)

}

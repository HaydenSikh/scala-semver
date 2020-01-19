package com.github.haydensikh.semver

import scala.math.Ordering

import com.github.haydensikh.semver.Version.preReleaseTagOrdering

object CrossVersionBridge {
  implicit class RichOrdering[T](underlying: Ordering[T]) {
     def orElse(other: Ordering[T]): Ordering[T] = (x, y) => {
       val res1 = underlying.compare(x, y)
       if (res1 != 0) res1 else other.compare(x, y)
     }
  }

  val versionOrdering: Ordering[Version] =
    Ordering.by[Version, Int](_.major)
      .orElse(Ordering.by(_.minor))
      .orElse(Ordering.by(_.patch))
      .orElse(preReleaseTagOrdering)
}
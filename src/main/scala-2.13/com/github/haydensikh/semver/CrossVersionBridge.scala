package com.github.haydensikh.semver

import com.github.haydensikh.semver.Version.preReleaseTagOrdering

private[semver] object CrossVersionBridge {
  val versionOrdering: Ordering[Version] =
    Ordering.by[Version, Int](_.major)
      .orElse(Ordering.by(_.minor))
      .orElse(Ordering.by(_.patch))
      .orElse(preReleaseTagOrdering)
}
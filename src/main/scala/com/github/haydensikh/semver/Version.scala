package com.github.haydensikh.semver

case class Version(
  major: Int,
  minor: Int,
  patch: Int,
  preReleaseTag: Option[String] = None,
  metadata: Seq[String] = Seq.empty
) {
  require(major >= 0, s"Invalid major version [$major].  It must be non-negative")
  require(minor >= 0, s"Invalid minor version [$minor].  It must be non-negative")
  require(patch >= 0, s"Invalid patch version [$patch].  It must be non-negative")

  override def toString: String = {
    val preReleaseStr = preReleaseTag.map("-" + _).getOrElse("")
    val metadataStr = if (metadata.nonEmpty) metadata.mkString("+", ".", "") else ""
    s"$major.$minor.$patch$preReleaseStr$metadataStr"
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: Version => implicitly[Ordering[Version]].equiv(this, that)
    case _ => false
  }

  def nextMajorVersion: Version = copy(major = major + 1, minor = 0, patch = 0)
  def nextMinorVersion: Version = copy(minor = minor + 1, patch = 0)
  def nextPatchVersion: Version = copy(patch = patch + 1)
}

object Version {
  private val preReleaseTagOrdering: Ordering[Version] = { (lhs, rhs) =>
    (lhs.preReleaseTag, rhs.preReleaseTag) match {
      case (None, None) => 0
      case (Some(_), None) => -1
      case (None, Some(_)) => 1
      case (Some(lTag), Some(rTag)) => lTag.compare(rTag)
    }
  }

  implicit val ordering: Ordering[Version] =
    Ordering.by[Version, Int](_.major)
      .orElse(Ordering.by(_.minor))
      .orElse(Ordering.by(_.patch))
      .orElse(preReleaseTagOrdering)
}

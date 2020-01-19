package com.github.haydensikh.semver

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class VersionTest extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {
  describe("""#nextMajorVersion""") {
    it("""increments the major version, sets minor and patch versions to 0""") {
      Version(0, 1, 2).nextMajorVersion should be (Version(1, 0, 0))
    }
  }

  describe("""#nextMinorVersion""") {
    it("""increments the minor version, sets patch version to 0""") {
      Version(0, 1, 2).nextMinorVersion should be (Version(0, 2, 0))
    }
  }

  describe("""#nextPatchVersion""") {
    it("""increments the patch version""") {
      Version(0, 1, 2).nextPatchVersion should be (Version(0, 1, 3))
    }
  }

  describe("""toString""") {
    it("""includes all components when specified""") {
      val version = Version(0, 1, 2, Some("SNAPSHOT"), Seq("build-1234", "foo", "bar"))
      val expected = "0.1.2-SNAPSHOT+build-1234.foo.bar"

      version.toString should be(expected)
    }

    it("""suppresses the "+" separator when there is no metadata""") {
      val version = Version(0, 1, 2, Some("SNAPSHOT"))
      val expected = "0.1.2-SNAPSHOT"

      version.toString should be(expected)
    }

    it("""suppresses the "-" separator when there is no pre-release tag""") {
      val version = Version(0, 1, 2, preReleaseTag = None, Seq("build-1234", "foo", "bar"))
      val expected = "0.1.2+build-1234.foo.bar"

      version.toString should be(expected)
    }

    it("""suppress all separators when there are no optional components""") {
      val version = Version(0, 1, 2)
      val expected = "0.1.2"

      version.toString should be(expected)
    }
  }

  describe("""implicit Ordering""") {
    it("""detects a version with a greater major version as greater than an otherwise identical version""") {
      forAll(Generate.version) { version =>
        val higherMajorVersion = version.copy(major = version.major + 1)
        higherMajorVersion should be > (version)
        version should be < (higherMajorVersion)
      }
    }

    it("""detects a version with a greater minor version as greater than an otherwise identical version""") {
      forAll(Generate.version) { version =>
        val higherMinorVersion = version.copy(minor = version.minor + 1)
        higherMinorVersion should be > (version)
        version should be < (higherMinorVersion)
      }
    }

    it("""detects a version with a greater patch version as greater than an otherwise identical version""") {
      forAll(Generate.version) { version =>
        val higherPatchVersion = version.copy(patch = version.patch + 1)
        higherPatchVersion should be > (version)
        version should be < (higherPatchVersion)
      }
    }

    it("""detects a version without a pre-release tag as greater than an otherwise identical version with a tag""") {
      forAll(Generate.version, Generate.preReleaseTag) { (version, preReleaseTag) =>
        val releaseVersion = version.copy(preReleaseTag = None)
        val preReleaseVersion = version.copy(preReleaseTag = Some(preReleaseTag))

        releaseVersion should be > (preReleaseVersion)
        preReleaseVersion should be < (releaseVersion)
      }
    }

    it("""ignores metadata for comparisons""") {
      forAll(
        Generate.version,
        Generate.metadata,
        Generate.metadata
      ) { (version, metadata1, metadata2) =>
        val version1 = version.copy(metadata = metadata1)
        val version2 = version.copy(metadata = metadata2)

        version1 should be (version2)
      }
    }
  }
}

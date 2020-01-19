package com.github.haydensikh.semver

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class VersionParserTest extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {
  describe(""".apply""") {
    describe("""success cases""") {
      it("""successfully parses a simple version""") {
        val input = "0.1.2"
        val expected = Version(0, 1, 2)
        val actual = VersionParser(input)

        actual should be(Right(expected))
        actual.map {
          _.metadata should contain theSameElementsInOrderAs(expected.metadata)
        }
      }

      it("""successfully parses a version with all optional components""") {
        val input = "0.1.2-RC2+qa.build-deadb33f"
        val expected = Version(0, 1, 2, Some("RC2"), Seq("qa", "build-deadb33f"))
        val actual = VersionParser(input)

        actual should be(Right(expected))
        actual.map {
          _.metadata should contain theSameElementsInOrderAs(expected.metadata)
        }
      }
    }

    describe("""error cases""") {
      it ("""fails to parse a major version with a leading zero""") {
        val input = "00.1.2"
        val expected = """'.' expected but '0' found. Column 2 of "00.1.2""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a non-numeric major version""") {
        val input = "a.1.2"
        val expected = """string matching regex '0|[1-9]\d*' expected but 'a' found. Column 1 of "a.1.2""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a minor version with a leading zero""") {
        val input = "0.01.2"
        val expected = """'.' expected but '1' found. Column 4 of "0.01.2""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a non-numeric minor version""") {
        val input = "0.a.2"
        val expected = """string matching regex '0|[1-9]\d*' expected but 'a' found. Column 3 of "0.a.2""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a patch version with a leading zero""") {
        val input = "0.1.02"
        val expected = """end of input expected. Column 6 of "0.1.02""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a non-numeric patch version""") {
        val input = "0.1.a"
        val expected = """string matching regex '0|[1-9]\d*' expected but 'a' found. Column 5 of "0.1.a""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }

      it ("""fails to parse a pre-release version that is only numeric""") {
        val input = "0.1.02-1234"
        val expected = """end of input expected. Column 6 of "0.1.02-1234""""
        val actual = VersionParser(input)

        actual should be (Left(expected))
      }
    }

    it("""parses a toString'd Version back to a fully equal instance""") {
      forAll(Generate.version) { original =>
        val recreated = VersionParser(original.toString)

        recreated should be (Right(original))
        recreated.map {
          _.metadata should contain theSameElementsInOrderAs(original.metadata)
        }
      }
    }
  }
}
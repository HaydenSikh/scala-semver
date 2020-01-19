package com.github.haydensikh.semver

import org.scalacheck.Gen

object Generate {
  private val digitChar: Gen[Char] = Gen.oneOf('0' to '9')
  private val nonDigitChar: Gen[Char] = Gen.oneOf(Gen.const('-'), Gen.alphaChar)
  private val identifierChar: Gen[Char] = Gen.oneOf(digitChar, nonDigitChar)

  // at least one non-digit character. prefix and suffix may be empty
  private val alphanumericIdentifier: Gen[String] =
    for {
      prefix <- Gen.listOf(identifierChar)
      suffix <- Gen.listOf(identifierChar)
      nonDigit <- nonDigitChar
    } yield {
      (prefix ++ Seq(nonDigit) ++ suffix).mkString
    }

  private val numericIdentifier: Gen[String] =
    Gen.choose(0, Int.MaxValue).map(_.toString)

  private val buildIdentifier: Gen[String] = Gen.oneOf(
    alphanumericIdentifier,
    Gen.nonEmptyListOf(digitChar).map(_.mkString)
  )

  val preReleaseTag: Gen[String] = Gen.oneOf(
    alphanumericIdentifier,
    numericIdentifier
  )

  val metadata: Gen[Seq[String]] = Gen.listOf(buildIdentifier)

  val version: Gen[Version] =
    for {
      major <- Gen.choose(0, Int.MaxValue)
      minor <- Gen.choose(0, Int.MaxValue)
      patch <- Gen.choose(0, Int.MaxValue)
      preRelease <- Gen.option(Generate.preReleaseTag)
      metadata <- Generate.metadata
    } yield {
      Version(major, minor, patch, preRelease, metadata)
    }
}

package com.github.haydensikh.semver

import scala.util.parsing.combinator.RegexParsers

object VersionParser {
  // use a nested object to so that the exposed API does not also expose all of
  // the RegexParser API
  private object HiddenParser extends RegexParsers {
    private def numericIdentifier: Parser[String] =
      """0|[1-9]\d*""".r ^^ {
        _.toString
      }

    private def alphanumericIdentifier: Parser[String] =
      """[a-zA-Z0-9-]*[a-zA-Z-][a-zA-Z0-9-]*""".r ^^ {
        _.toString
      }

    private def digits: Parser[String] =
      """[0-9]+""".r ^^ {
        _.toString
      }

    private def preReleaseIdentifier: Parser[String] =
      alphanumericIdentifier | numericIdentifier

    private def buildIdentifier: Parser[String] =
      alphanumericIdentifier | digits

    private def dotSeparatedBuildIdentifiers: Parser[Seq[String]] =
      rep1sep(buildIdentifier, ".")

    private def major: Parser[Int] = numericIdentifier.map(_.toInt)

    private def minor: Parser[Int] = numericIdentifier.map(_.toInt)

    private def patch: Parser[Int] = numericIdentifier.map(_.toInt)

    private def core: Parser[(Int, Int, Int)] =
      (major ~ "." ~ minor ~ "." ~ patch) ^^ {
        case major ~ _ ~ minor ~ _ ~ patch => (major, minor, patch)
      }

    private def fullVersion: Parser[Version] =
      core ~ "-" ~ preReleaseIdentifier ~ "+" ~ dotSeparatedBuildIdentifiers ^^ {
        case (major, minor, patch) ~ "-" ~ tag ~ "+" ~ build =>
          Version(major, minor, patch, Some(tag), build)
      }

    private def versionWithBuild: Parser[Version] =
      core ~ "+" ~ dotSeparatedBuildIdentifiers ^^ {
        case (major, minor, patch) ~ "+" ~ build =>
          Version(major, minor, patch, None, build)
      }

    private def versionWithTag: Parser[Version] =
      core ~ "-" ~ preReleaseIdentifier ^^ {
        case (major, minor, patch) ~ "-" ~ tag =>
          Version(major, minor, patch, Some(tag))
      }

    private def simpleVersion: Parser[Version] =
      core ^^ {
        case (major, minor, patch) => Version(major, minor, patch)
      }

    private def version: Parser[Version] =
      phrase(fullVersion | versionWithBuild | versionWithTag | simpleVersion)

    def apply(in: CharSequence): Either[String, Version] =
      parseAll(version, in) match {
        case NoSuccess(err, next) =>
          Left(s"""$err. Column ${next.pos.column} of "$in"""")
        case Success(ver, _) =>
          Right(ver)
      }
  }

  def apply(in: CharSequence): Either[String, Version] = HiddenParser.apply(in)
}

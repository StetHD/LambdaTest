import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

name := "lambda-test"

organization := "com.fortysevendeg"

version := "1.3.0"

scalaVersion := "2.12.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

viewSettings

testFrameworks += new TestFramework("com.fortysevendeg.lambdatest.sbtinterface.LambdaFramework")

fork in Test := true

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "org.scala-sbt" % "test-interface" % "1.0",
  "com.typesafe" % "config" % "1.3.1"
)

// Scalariform forces indent after infix plus (with no option to override)
// This makes tests less readable
// So tests include # format: OFF

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
.setPreference(SpacesWithinPatternBinders, true)
.setPreference(SpaceBeforeColon, false)
.setPreference(SpaceInsideParentheses, false)
.setPreference(SpaceInsideBrackets, false)
.setPreference(SpacesAroundMultiImports, true)
.setPreference(PreserveSpaceBeforeArguments, false)
.setPreference(CompactStringConcatenation, false)
.setPreference(DanglingCloseParenthesis, Force)
.setPreference(CompactControlReadability, false)
.setPreference(AlignParameters, false)
.setPreference(AlignArguments, true)
.setPreference(AlignSingleLineCaseStatements, false)
.setPreference(DoubleIndentClassDeclaration, false)
.setPreference(IndentLocalDefs, false)
.setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
.setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
.setPreference(RewriteArrowSymbols, true)


lazy val publishSnapshot = taskKey[Unit]("Publish only if the version is a SNAPSHOT")

publishSnapshot := Def.taskDyn {
  if (isSnapshot.value) Def.task {
    PgpKeys.publishSigned.value
  }
  else Def.task(println("Actual version is not a Snapshot. Skipping publish."))
}.value

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("Snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("Releases" at nexus + "service/local/staging/deploy/maven2")
}

organizationName := "47 Degrees"

organizationHomepage := Some(new URL("http://47deg.com"))

lazy val micrositeSettings = Seq(
  micrositeName := "LambdaTest",
  micrositeDescription := "Functional Scala testing",
  micrositeBaseUrl := "LambdaTest",
  micrositeDocumentationUrl := "/LambdaTest/index.html",
  micrositeGithubOwner := "47deg",
  micrositeGithubRepo := "LambdaTest",
  micrositeHighlightTheme := "rainbow",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.md",
  micrositePalette := Map(
    "brand-primary" -> "#01C2C2",
    "brand-secondary" -> "#142236",
    "brand-tertiary" -> "#202D40",
    "gray-dark" -> "#383D44",
    "gray" -> "#646D7B",
    "gray-light" -> "#E6E7EC",
    "gray-lighter" -> "#F4F5F9",
    "white-color" -> "#E6E7EC"),
     //siteSubdirName in ScalaUnidoc := "api",
     //unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(lambdatest),
     git.remoteRepo := "git@github.com:47deg/LambdaTest.git",
     autoAPIMappings := true,
     docsMappingsAPIDir := "api",
     addMappingsToSiteDir(mappings in (ScalaUnidoc,packageDoc), docsMappingsAPIDir)
)

lazy val docsMappingsAPIDir = settingKey[String](
  "Name of subdirectory in site target directory for api docs")

lazy val noPublishSettings: Seq[Setting[_]] = Seq(
  publish := {},
  publishLocal := {}
)

lazy val docs = (project in file("docs"))
.settings(micrositeSettings: _*)
.settings(unidocSettings)
.settings(
  name := "docs",
  description := "LambdaTest docs"
)
.settings(noPublishSettings)
.enablePlugins(MicrositesPlugin)

lazy val gpgFolder = sys.env.getOrElse("GPG_FOLDER", ".")

pgpPassphrase := Some(sys.env.getOrElse("GPG_PASSPHRASE", "").toCharArray)

pgpPublicRing := file(s"$gpgFolder/pubring.gpg")

pgpSecretRing := file(s"$gpgFolder/secring.gpg")

credentials += Credentials("Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("PUBLISH_USERNAME", ""),
  sys.env.getOrElse("PUBLISH_PASSWORD", ""))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/47deg/LambdaTest"))

scmInfo := Some(ScmInfo(url("https://github.com/47deg/LambdaTest"), "scm:git@github.com:47deg/LambdaTest.git"))

pomExtra := (
<developers>
  <developer>
    <name>47 Degrees (twitter: @47deg)</name>
    <email>hello@47deg.com</email>
    <url>http://http://www.47deg.com</url>
  </developer>
</developers>
)

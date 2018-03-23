scalaVersion := "2.12.4"
scalacOptions ++= Seq("-unchecked", "-deprecation")
resolvers ++= Seq(
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  ivyResolver("bintray-sbt-plugin-releases", url("https://dl.bintray.com/content/sbt/sbt-plugin-releases")),
  ivyResolver("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))
)

addSbtPlugin("com.malliina" % "sbt-utils" % "0.8.0")

def ivyResolver(name: String, repoUrl: sbt.URL) =
  Resolver.url(name, repoUrl)(Resolver.ivyStylePatterns)

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
    "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
    "wiquery-maven-repo" at "http://wiquery.googlecode.com/svn/repo/",
    "Twitter Repository" at "http://maven.twttr.com/",
    Classpaths.typesafeResolver,
    Resolver.url(
      "sbt-plugin-releases",
      new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns)
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11"))

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

addSbtPlugin("com.twitter" % "sbt-package-dist" % "1.0.5")

addSbtPlugin("org.clapper" % "sbt-izpack" % "0.3.2")




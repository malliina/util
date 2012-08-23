scalaVersion := "2.9.2"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
    "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
    "Twitter Repository" at "http://maven.twttr.com/",
    Classpaths.typesafeResolver,
    Resolver.url(
      "sbt-plugin-releases",
      new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns)
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11.1"))

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

//addSbtPlugin("com.twitter" % "sbt-package-dist" % "1.0.5")

//addSbtPlugin("org.clapper" % "sbt-izpack" % "0.3.2")

addSbtPlugin("com.typesafe" % "sbt-native-packager" % "0.4.4")





scalaVersion := "2.9.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "wiquery-maven-repo" at "http://wiquery.googlecode.com/svn/repo/"

resolvers += "Twitter Repository" at "http://maven.twttr.com/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11"))

addSbtPlugin("com.twitter" % "sbt-package-dist" % "1.0.5")



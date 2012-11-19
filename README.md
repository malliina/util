An exploration of Scala. Test code for wicket, play!, web sockets, actors, functional programming, and whatever seems interesting.

To do:

- Refactor SBT packaging settings into an independent SBT plugin, that is, sbt-packager
- A sane container class for sbt-native-packager mappings that spits out settings for rpm, deb, windows
- Evaluate whether it makes sense to have DB operations that return Promise[ResultSet]
- Evaluate Slick after 2.10 is released
- Play fucks up the logback configuration jesus christ

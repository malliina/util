import java.nio.file._
import sbt._
import inc.Analysis
import Keys._
import com.mle.util.FileUtilities

/**
 * Builds packages containing all the jars, libs, etc. Based on
 * https://github.com/twitter/sbt-package-dist/blob/master/src/main/scala/com/twitter/sbt/PackageDist.scala
 * however that example depends on Git while this code does not.
 * @author Mle
 */
object Packaging extends Plugin {
  // Relative to the project
  val outDir = "distrib"
  val confDir = "conf"
  val libDir = "lib"
  val scriptDir = "scripts"
  val libOutDir = outDir + "/" + libDir
  val confOutDir = outDir + "/" + confDir
  val scriptOutDir = outDir + "/" + scriptDir
  val packageKey = packageBin in Compile
  // Tasks and keys
  val compileKey: TaskKey[Analysis] = compile in Compile
  val deployTask = TaskKey[File]("deploy", "Compiles the sources, packages a .jar and deploys the .jar to a server")
  val basePath = SettingKey[Path]("base-path", "Same as base-directory")
  val distribDir = SettingKey[Path]("package-dist-dir", "The directory to package dists into")
  val configPath = SettingKey[Option[Path]]("config-path", "Location of config files (if any)")
  val configOutputPath = SettingKey[Option[Path]]("config-output-path", "Output location of config files (if any)")
  val scriptPath = SettingKey[Option[Path]]("script-path", "Location of scripts (if any)")
  val scriptOutPath = SettingKey[Option[Path]]("script-output-path", "Output location of scripts (if any)")
  val configFiles = TaskKey[Set[Path]]("config-files", "Config files to package with the app")
  val scriptFiles = TaskKey[Set[Path]]("scripts", "Scripts to package with the app")
  val copyConfs = TaskKey[Set[Path]]("copy-confs", "Copies all configuration files to " + confOutDir)
  val copyScripts = TaskKey[Set[Path]]("copy-scripts", "Copies all configuration files to " + scriptOutDir)
  val printLibs = TaskKey[Unit]("print-libs", "Prints library .jars to stdout")
  val libs = TaskKey[Set[Path]]("libs", "All (managed and unmanaged) libs")
  val copyLibs = TaskKey[Set[Path]]("copy-libs", "Copies all (managed and unmanaged) libs to " + libOutDir)
  val createJar = TaskKey[Set[Path]]("create-jar", "Copies application .jar to " + outDir)
  val packageApp = TaskKey[Set[Path]]("package-app", "Copies the app (jars, libs, confs) to " + outDir)
  val bat = TaskKey[Set[Path]]("bat", "Copies the app (jars, libs, confs) along with a .bat file to " + outDir)
  val sh = TaskKey[Set[Path]]("sh", "Copies the app (jars, libs, confs) along with a .sh file to " + outDir)
  val zip = TaskKey[File]("zip", "Creates a zip of the app to " + outDir)
  // Codify what the tasks do
  // Enables "package-war" to create a .war of the whole app, and creates static content out of src/main/resources/publicweb (in addition to the default of src/main/webapp)
  //  val warSettings = webSettings ++ Seq(webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb")) ++ Seq(libraryDependencies ++= Seq(Dependencies.jettyContainer))
  val newSettings = Seq(
    exportJars := true,
    basePath <<= (baseDirectory)(b => b.toPath),
    distribDir <<= (basePath)(b => (b resolve outDir)),
    configPath <<= (basePath)(b => Some((b resolve confDir))),
    scriptPath <<= (basePath)(b => Some((b resolve scriptDir))),
    configOutputPath <<= (distribDir)(d => Some((d resolve confDir))),
    configFiles <<= filesIn(configPath),
    scriptFiles <<= filesIn(scriptPath),
    copyConfs <<= copyTask(configFiles),
    copyScripts <<= copyTask(scriptFiles),
    libs <<= (
      dependencyClasspath in Runtime,
      exportedProducts in Compile
      ) map ((cp, products) => {
      // Libs, but not my own jars
      cp.files.filter(f => !products.files.contains(f)).map(_.toPath).toSet
    }),
    printLibs <<= (libs) map ((l) => {
      l foreach println
    }),
    copyLibs <<=(
      libs,
      distribDir
    ) map ((libJars, dest) => {
      val libDestination = dest resolve libDir
      Files.createDirectories(libDestination)
      libJars.map(libJar => Files.copy(libJar, libDestination resolve libJar.getFileName, StandardCopyOption.REPLACE_EXISTING))
    }),
    createJar <<= (
      exportedProducts in Compile,
      distribDir,
      name,
      version,
      scalaVersion
      ) map ((products, dest, appName, appVer, scalaVer) => {
      val versionSuffix = "_" + scalaVer + "-" + appVer
      val jarPaths = products.files.map(_.toPath).toSet
      jarPaths.map(jarPath => Files.copy(jarPath, (dest resolve stripSection(jarPath.getFileName.toString, versionSuffix)), StandardCopyOption.REPLACE_EXISTING))
    }),
    packageApp <<= (
      copyLibs,
      createJar,
      copyConfs,
      copyScripts
      ) map ((libs, jars, confs, scripts) => {
      libs ++ jars ++ confs ++ scripts
    }),
    bat <<= (distribDir, name, packageApp, copyScripts) map ((appDir, appName, appFiles, scripts) => {
      launcher(appDir, scripts, appName, ".bat", appFiles)
    }),
    sh <<= (distribDir, name, packageApp, copyScripts) map ((appDir, appName, appFiles, scripts) => {
      launcher(appDir, scripts, appName, ".sh", appFiles)
    }),
    zip <<= (
      baseDirectory,
      packageApp,
      distribDir,
      name
      ) map ((base, files, distribDir, appName) => {
      val zipFile = base / outDir / (appName + ".zip")
      val rebaser = sbt.Path.rebase(distribDir.toFile, "")
      val filez = files.map(_.toFile)
      IO.zip(filez.map(f => (f, rebaser(f).get)), zipFile)
      println("Packaged: " + zipFile)
      zipFile
    }),
    deployTask <<= packageKey map ((packageResult: File) => {
      println("Packaged: " + packageResult.getAbsolutePath)
      println("Deployment not yet implemented...")
      packageResult
    })
  )

  def launcher(appDir: Path,
               files: Types.Id[Set[Path]],
               appName: String,
               extension: String,
               appFiles: Types.Id[Set[Path]]) = {
    val launcherFilename = appName.toLowerCase + extension
    val launcherDestination = appDir resolve launcherFilename
    val maybeLauncherFile = files.find(_.getFileName.toString == launcherFilename)
    if (maybeLauncherFile.isDefined) {
      Files.copy(maybeLauncherFile.get, launcherDestination, StandardCopyOption.REPLACE_EXISTING)
      println("Launcher: " + launcherDestination)
    } else {
      println("Did not find: " + launcherFilename)
    }
    appFiles
  }

  // Helpers
  def copyTask(files: TaskKey[Set[Path]]) = (
    basePath,
    files,
    distribDir
    ) map (FileUtilities.copy)

  def filesIn(dir: SettingKey[Option[Path]]): Project.Initialize[Task[Set[Path]]] = dir.map((path: Option[Path]) => {
    path.map(p => if (Files isDirectory p) FileUtilities.listPaths(p).toSet[Path] else Set.empty[Path]).getOrElse(Set.empty[Path])
  })

  /**
   * Removes section from name
   */
  def stripSection(name: String, section: String) = if (name.contains(section) && name.endsWith(".jar")) name.slice(0, name indexOf section) + ".jar" else name

}
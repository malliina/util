import java.nio.file._
import sbt._
import Keys._
import com.mle.util.FileUtilities
import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import linux.LinuxPackageMapping

/**
 * Builds packages containing all the jars, libs, etc. Based on
 * https://github.com/twitter/sbt-package-dist/blob/master/src/main/scala/com/twitter/sbt/PackageDist.scala
 * however that example depends on Git while this code does not.
 * @author Mle
 */
object Packaging extends Plugin {
  implicit def path2path(path: Path) = new {
    def /(next: String) = path resolve next

    def /(next: Path) = path resolve next
  }

  // Relative to the project
  val outDir = "distrib"
  val confDir = "conf"
  val libDir = "lib"
  val scriptDir = "scripts"
  val libOutDir = outDir + "/" + libDir
  val confOutDir = outDir + "/" + confDir
  val scriptOutDir = outDir + "/" + scriptDir
  val packageKey = packageBin in Compile
  // Settings
  val basePath = SettingKey[Path]("base-path", "Same as base-directory")
  val distribDir = SettingKey[Path]("package-dist-dir", "The directory to package dists into")
  val configPath = SettingKey[Option[Path]]("config-path", "Location of config files (if any)")
  val configOutputPath = SettingKey[Option[Path]]("config-output-path", "Output location of config files (if any)")
  val scriptPath = SettingKey[Option[Path]]("script-path", "Location of scripts (if any)")
  val scriptOutPath = SettingKey[Option[Path]]("script-output-path", "Output location of scripts (if any)")
  // Native
  val unixHome = SettingKey[Path]("unix-home", "Home dir on unix")
  val unixLibHome = SettingKey[Path]("unix-lib-home", "Lib dir on unix")
  val unixConfHome = SettingKey[Path]("unix-conf-home", "Conf dir on unix")
  val unixScriptHome = SettingKey[Path]("unix-script-home", "Script dir on unix")
  val unixLogDir = SettingKey[Path]("unix-log-home", "Log dir on unix")
  val pkgSrcHome = SettingKey[Path]("pkg-src-home", "Packaging home directory")
  val controlDir = SettingKey[Path]("control-dir", "Directory for control files for native packaging")
  val preInstall = SettingKey[Path]("pre-install", "Preinstall script")
  val postInstall = SettingKey[Path]("post-install", "Postinstall script")
  val preRemove = SettingKey[Path]("pre-remove", "Preremove script")
  val postRemove = SettingKey[Path]("post-remove", "Postremove script")
  // Tasks
  val appJar = TaskKey[Path]("app-jar", "The application jar")
  val defaultsFile = TaskKey[Path]("defaults-file", "The defaults config file")
  val configFiles = TaskKey[Seq[Path]]("config-files", "Config files to package with the app")
  val scriptFiles = TaskKey[Seq[Path]]("scripts", "Scripts to package with the app")
  val copyConfs = TaskKey[Seq[Path]]("copy-confs", "Copies all configuration files to " + confOutDir)
  val copyScripts = TaskKey[Seq[Path]]("copy-scripts", "Copies all configuration files to " + scriptOutDir)
  val printLibs = TaskKey[Unit]("print-libs", "Prints library .jars to stdout")
  val libs = TaskKey[Seq[Path]]("libs", "All (managed and unmanaged) libs")
  val copyLibs = TaskKey[Seq[Path]]("copy-libs", "Copies all (managed and unmanaged) libs to " + libOutDir)
  val createJar = TaskKey[Seq[Path]]("create-jar", "Copies application .jar to " + outDir)
  val packageApp = TaskKey[Seq[Path]]("package-app", "Copies the app (jars, libs, confs) to " + outDir)
  val bat = TaskKey[Seq[Path]]("bat", "Copies the app (jars, libs, confs) along with a .bat file to " + outDir)
  val sh = TaskKey[Seq[Path]]("sh", "Copies the app (jars, libs, confs) along with a .sh file to " + outDir)
  val zip = TaskKey[File]("zip", "Creates a zip of the app to " + outDir)
  // Native tasks
  val libMappings = TaskKey[Seq[(Path, String)]]("lib-mappings", "Libs mapped to paths")
  val confMappings = TaskKey[Seq[(Path, String)]]("conf-mappings", "Confs mapped to paths")
  val scriptMappings = TaskKey[Seq[(Path, String)]]("script-mappings", "Scripts mapped to paths")
  val launcherMapping = TaskKey[(Path, String)]("launcher-mapping", "Launcher jar file path")
  val initdMapping = SettingKey[(Path, String)]("initd-mapping", "/etc/init.d start script")
  val defaultsMapping = SettingKey[(Path, String)]("defaults-mapping", "Defaults file path")
  val debFiles = TaskKey[Seq[String]]("deb-files", "Files on Debian")
  val rpmFiles = TaskKey[Seq[String]]("rpm-files", "Files on RPM")
  // Codify what the tasks do
  // Enables "package-war" to create a .war of the whole app, and creates static content out of src/main/resources/publicweb (in addition to the default of src/main/webapp)
  //  val warSettings = webSettings ++ Seq(webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb")) ++ Seq(libraryDependencies ++= Seq(Dependencies.jettyContainer))
  val newSettings = Seq(
    unixHome <<= (name)(pkgName => Paths get "/opt/" + pkgName),
    unixLibHome <<= (unixHome)(_ / "lib"),
    unixConfHome <<= (unixHome)(_ / "conf"),
    unixScriptHome <<= (unixHome)(_ / "scripts"),
    unixLogDir <<= (unixHome)(_ / "logs"),
    pkgSrcHome <<= (basePath)(_ / "src/pkg"),
    controlDir <<= (pkgSrcHome)(_ / "control"),
    preInstall <<= (controlDir)(_ / "preinstall.sh"),
    postInstall <<= (controlDir)(_ / "postinstall.sh"),
    preRemove <<= (controlDir)(_ / "preremove.sh"),
    postRemove <<= (controlDir)(_ / "postremove.sh"),
    libMappings <<= (libs, unixLibHome) map ((libFiles, destDir) => {
      libFiles.map(file => file -> (destDir / file.getFileName).toString)
    }),
    confMappings <<= (configFiles, configPath, unixConfHome) map rebase,
    scriptMappings <<= (scriptFiles, scriptPath, unixScriptHome) map rebase,
    launcherMapping <<= (appJar, unixHome, name) map ((jar, home, pkgName) => jar -> (home / (pkgName + ".jar")).toString),
    initdMapping <<= (pkgSrcHome, name)((base, pkgName) => {
      (base / (pkgName + ".sh")) -> ("/etc/init.d/" + pkgName)
    }),
    defaultsMapping <<= (pkgSrcHome, name)((base, pkgName) => {
      (base / (pkgName + ".defaults")) -> ("/etc/default/" + pkgName)
    }),
    confMappings <<= (configFiles, configPath, unixConfHome) map rebase,
    basePath <<= (baseDirectory)(b => b.toPath),
    distribDir <<= (basePath)(b => (b resolve outDir)),
    configPath <<= (basePath)(b => Some((b resolve confDir))),
    scriptPath <<= (basePath)(b => Some((b resolve scriptDir))),
    configOutputPath <<= (distribDir)(d => Some((d resolve confDir))),
    configFiles <<= filesIn(configPath),
    scriptFiles <<= filesIn(scriptPath),
    appJar <<= (exportedProducts in Compile, name) map ((jars: Classpath, pkgName) => jars.files.head.toPath),
    copyConfs <<= copyTask(configFiles),
    copyScripts <<= copyTask(scriptFiles),
    libs <<= (
      dependencyClasspath in Runtime,
      exportedProducts in Compile
      ) map ((cp, products) => {
      // Libs, but not my own jars
      cp.files.filter(f => !products.files.contains(f)).map(_.toPath)
    }),
    printLibs <<= (libs, name) map ((l: Seq[Path], pkgName) => {
      l foreach println
    }),
    copyLibs <<= (
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
      Files.createDirectories(dest)
      val versionSuffix = "_" + scalaVer + "-" + appVer
      val jarPaths = products.files.map(_.toPath)
      jarPaths.map(jarPath => {
        Files.copy(jarPath, (dest resolve stripSection(jarPath.getFileName.toString, versionSuffix)), StandardCopyOption.REPLACE_EXISTING)
      })
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
      Files.createDirectories(distribDir)
      val zipFile = base / outDir / (appName + ".zip")
      val rebaser = sbt.Path.rebase(distribDir.toFile, "")
      val filez = files.map(_.toFile)
      IO.zip(filez.map(f => (f, rebaser(f).get)), zipFile)
      println("Packaged: " + zipFile)
      zipFile
    }),
    debFiles <<= (debian.Keys.linuxPackageMappings in Debian, name) map ((mappings, pkgName) => {
      printMappingDestinations(mappings)
    }),
    rpmFiles <<= (rpm.Keys.linuxPackageMappings in Rpm, name) map ((mappings, pkgName) => {
      printMappingDestinations(mappings)
    })
  )

  def printMappingDestinations(mappings: Seq[LinuxPackageMapping]) = {
//    mappings.foreach(mapping => {
//      mapping.mappings.foreach(pair => {
//        val (file, dest) = pair
//        println("file: " + file + ", dest: " + dest  )
//      })
//    })
    val ret = mappings.flatMap(_.mappings.map(_._2))
    ret foreach println
    ret
  }

  def launcher(appDir: Path,
               files: Types.Id[Seq[Path]],
               appName: String,
               extension: String,
               appFiles: Types.Id[Seq[Path]]) = {
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
  def copyTask(files: TaskKey[Seq[Path]]) = (
    basePath,
    files,
    distribDir
    ) map ((base, filez, dest) => FileUtilities.copy(base, filez.toSet, dest).toSeq)

  def filesIn(dir: SettingKey[Option[Path]]): Project.Initialize[Task[Seq[Path]]] = (dir, name).map((path: Option[Path], pkgName) => {
    path.map(p => if (Files isDirectory p) FileUtilities.listPaths(p) else Seq.empty[Path]).getOrElse(Seq.empty[Path])
  })

  /**
   * Removes section from name
   */
  def stripSection(name: String, section: String) =
    if (name.contains(section) && name.endsWith(".jar"))
      name.slice(0, name indexOf section) + ".jar"
    else
      name

  def rebase(file: Path, srcBase: Path, destBase: Path) = destBase resolve (srcBase relativize file)

  def rebase(files: Seq[Path], srcBase: Path, destBase: Path): Seq[(Path, String)] =
    files map (file => file -> rebase(file, srcBase, destBase).toString)

  def rebase(files: Seq[Path], maybeSrcBase: Option[Path], destBase: Path): Seq[(Path, String)] =
    maybeSrcBase.map(srcBase => rebase(files, srcBase, destBase)).getOrElse(Seq.empty[(Path, String)])
}
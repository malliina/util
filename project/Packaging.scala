import java.nio.file._
import sbt._
import Keys._
import com.mle.util.FileUtilities
import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import Implicits._
import linux.LinuxPackageMapping
import scala.Some

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
  val appJarAsFile = packageBin in Compile
  // Settings
  val basePath = SettingKey[Path]("base-path", "Same as base-directory")
  val distribDir = SettingKey[Path]("package-dist-dir", "The directory to package dists into")
  val configPath = SettingKey[Option[Path]]("config-path", "Location of config files (if any)")
  val configOutputPath = SettingKey[Option[Path]]("config-output-path", "Output location of config files (if any)")
  val scriptPath = SettingKey[Option[Path]]("script-path", "Location of scripts (if any)")
  val scriptOutPath = SettingKey[Option[Path]]("script-output-path", "Output location of scripts (if any)")
  // Native settings
  val unixHome = SettingKey[Path]("unix-home", "Home dir on unix")
  val unixLibDest = SettingKey[Path]("unix-lib-home", "Lib dir on unix")
  val unixConfDest = SettingKey[Path]("unix-conf-home", "Conf dir on unix")
  val unixScriptDest = SettingKey[Path]("unix-script-home", "Script dir on unix")
  val unixLogDir = SettingKey[Path]("unix-log-home", "Log dir on unix")
  val pkgHome = SettingKey[Path]("pkg-home", "Packaging home directory")
  val unixPkgHome = SettingKey[Path]("unix-pkg-home", "Unix packaging directory")
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
  val debFiles = TaskKey[Seq[String]]("deb-files", "Files on Debian")
  val rpmFiles = TaskKey[Seq[String]]("rpm-files", "Files on RPM")
  val appJarName = SettingKey[String]("app-jar-name", "Main app jar on destination")
  val homeVar = SettingKey[String]("home-var", "Application home environment variable")


  //  val mainClass = SettingKey[String]("main-class","The main class (for .exe wrapper)")

  // Codify what the tasks do
  // Enables "package-war" to create a .war of the whole app, and creates static content out of src/main/resources/publicweb (in addition to the default of src/main/webapp)
  //  val warSettings = webSettings ++ Seq(webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb")) ++ Seq(libraryDependencies ++= Seq(Dependencies.jettyContainer))
  val newSettings = Seq(
    appJarName <<= (name)(_ + ".jar"),
    homeVar <<= (name)(_.toUpperCase + "_HOME"),
    //    winSwConf <<= (windowsPkgHome,name)(_ / "winsw-1.9-bin.exe"),
    basePath <<= (baseDirectory)(_.toPath),
    // Standard directory layout
    unixHome <<= (name)(pkgName => Paths get "/opt/" + pkgName),
    unixLibDest <<= (unixHome)(_ / "lib"),
    unixConfDest <<= (unixHome)(_ / "conf"),
    unixScriptDest <<= (unixHome)(_ / "scripts"),
    unixLogDir <<= (unixHome)(_ / "logs"),
    pkgHome <<= (basePath)(_ / "src" / "pkg"),
    unixPkgHome <<= (pkgHome)(_ / "unix"),
    // rpm/deb postinst control files
    controlDir <<= (unixPkgHome)(_ / "control"),
    preInstall <<= (controlDir)(_ / "preinstall.sh"),
    postInstall <<= (controlDir)(_ / "postinstall.sh"),
    preRemove <<= (controlDir)(_ / "preuninstall.sh"),
    postRemove <<= (controlDir)(_ / "postuninstall.sh"),
    distribDir <<= (basePath)(b => b / outDir),
    configPath <<= (basePath)(b => Some((b / confDir))),
    scriptPath <<= (basePath)(b => Some((b / scriptDir))),
    configOutputPath <<= (distribDir)(d => Some((d / confDir))),
    configFiles <<= filesIn(configPath),
    scriptFiles <<= filesIn(scriptPath),
    appJar <<= (packageBin in Compile, name) map ((jarFile, pkgName) => jarFile.toPath),
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
      printMappings(mappings)
    }),
    rpmFiles <<= (rpm.Keys.linuxPackageMappings in Rpm, name) map ((mappings, pkgName) => {
      printMappings(mappings)
    })
  )

  def printMappings(mappings: Seq[LinuxPackageMapping]) = {
    mappings.foreach(mapping => {
      mapping.mappings.foreach(pair => {
        val (file, dest) = pair
        val fileType = if (file.isFile) "file"
        else {
          if (file.isDirectory) "dir" else "UNKNOWN"
        }
        println(fileType + ": " + file + ", dest: " + dest)
      })
    })
    val ret = mappings.flatMap(_.mappings.map(_._2))
    ret foreach println
    ret
  }

  def launcher(appDir: Path,
               files: Seq[Path],
               appName: String,
               extension: String,
               appFiles: Seq[Path]) = {
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
    path.map(p =>
      if (Files isDirectory p) FileUtilities.listPaths(p)
      else Seq.empty[Path]
    ).getOrElse(Seq.empty[Path])
  })

  /**
   * Removes section from name
   */
  def stripSection(name: String, section: String) =
    if (name.contains(section) && name.endsWith(".jar"))
      name.slice(0, name indexOf section) + ".jar"
    else
      name
}
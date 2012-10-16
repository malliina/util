import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import java.nio.file.Path
import linux.LinuxPackageMapping
import sbt.Keys._
import sbt._
import Packaging._
import Implicits._

object NativePackaging {
  val linuxSettings: Seq[Setting[_]] = Seq(
    // Flat copy of libs to /lib on destination system
    libMappings <<= (libs, unixLibHome) map ((libFiles, destDir) => {
      libFiles.map(file => file -> (destDir / file.getFileName).toString)
    }),
    confMappings <<= (configFiles, configPath, unixConfHome) map rebase,
    scriptMappings <<= (scriptFiles, scriptPath, unixScriptHome) map rebase,
    confMappings <<= (configFiles, configPath, unixConfHome) map rebase,

    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
    linux.Keys.packageSummary := "This is a summary of the package",
    linux.Keys.packageDescription := "This is the description of the package.",
    //    name := "wicket",
    linux.Keys.linuxPackageMappings in Linux <++= (
      unixHome, pkgSrcHome, name, packageKey, libMappings, confMappings,
      scriptMappings, unixLogDir
      ) map (
      (home, pkgSrc, pkgName, jarFile, libs, confs, scripts, logDir) => Seq(
        pkgMaps(Seq((pkgSrc / (pkgName + ".sh")) -> ("/etc/init.d/" + pkgName)) ++ scripts, perms = "0755"),
        pkgMaps(libs),
        pkgMaps(confs ++ Seq((pkgSrc / (pkgName + ".defaults")) -> ("/etc/default/" + pkgName)), isConfig = true),
        pkgMap((pkgSrc / "logs") -> logDir.toString, perms = "0755"),
        pkgMap(jarFile.toPath -> ((home / (pkgName + ".jar")).toString))
      ))
  )
  val debianSettings: Seq[Setting[_]] = Seq(
    debian.Keys.linuxPackageMappings in Debian <++= linux.Keys.linuxPackageMappings in Linux,
    debian.Keys.version := "0.1",
    debian.Keys.linuxPackageMappings in Debian <++= (pkgSrcHome, name,
      preInstall, postInstall, preRemove, postRemove) map (
      (pkgSrc, pkgName, preinst, postinst, prerm, postrm) => Seq(
        // http://lintian.debian.org/tags/no-copyright-file.html
        pkgMap((pkgSrc / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright")),
        pkgMap((pkgSrc / "changelog") -> ("/usr/share/doc/" + pkgName + "/changelog.gz"), gzipped = true) asDocs(),
        pkgMaps(Seq(
          preinst -> "DEBIAN/preinst",
          postinst -> "DEBIAN/postinst",
          prerm -> "DEBIAN/prerm",
          postrm -> "DEBIAN/postrm"
        ), perms = "0755")
      ))
    ,
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget")

  )
  val rpmSettings: Seq[Setting[_]] = Seq(
    rpm.Keys.linuxPackageMappings in Rpm <++= linux.Keys.linuxPackageMappings in Linux,
    rpm.Keys.rpmRelease := "0.1",
    rpm.Keys.rpmVendor := "kingmichael",
    rpm.Keys.rpmLicense := Some("You have the right to remain silent"),
    rpm.Keys.rpmPreInstall <<= (preInstall)(Some(_)),
    rpm.Keys.rpmPostInstall <<= (postInstall)(Some(_)),
    rpm.Keys.rpmPreRemove <<= (preRemove)(Some(_)),
    rpm.Keys.rpmPostRemove <<= (postRemove)(Some(_))
  )
  val windowsSettings: Seq[Setting[_]] = Seq(
    // Windows
    windows.Keys.wixFile := new File("doesnotexist")
  )
  val defaultNativeProject: Seq[Setting[_]] = linuxSettings ++ debianSettings ++ rpmSettings ++ windowsSettings

  def pkgMap(file: (Path, String), perms: String = "0644", gzipped: Boolean = false) =
    pkgMaps(Seq(file), perms = perms, gzipped = gzipped)

  def pkgMaps(files: Seq[(Path, String)],
              user: String = "root",
              group: String = "root",
              perms: String = "0644",
              isConfig: Boolean = false,
              gzipped: Boolean = false) = {
    var mapping = LinuxPackageMapping(files.map(pair => pair._1.toFile -> pair._2)) withUser user withGroup group withPerms perms
    //    printMapping(mapping)
    if (isConfig)
      mapping = mapping withConfig()
    if (gzipped)
      mapping = mapping.gzipped
    mapping
  }

  def pkgMapping(files: (Path, String)*) = {
    packageMapping(files.map(pair => pair._1.toFile -> pair._2): _*)
    packageMapping()
  }

  def printMapping(mapping: LinuxPackageMapping) {
    mapping.mappings.foreach(ping => {
      val (file, dest) = ping
      println("file: " + file + ", dest: " + dest)
    })
  }

  def rebase(file: Path, srcBase: Path, destBase: Path) = destBase resolve (srcBase relativize file)

  def rebase(files: Seq[Path], srcBase: Path, destBase: Path): Seq[(Path, String)] =
    files map (file => file -> rebase(file, srcBase, destBase).toString)

  def rebase(files: Seq[Path], maybeSrcBase: Option[Path], destBase: Path): Seq[(Path, String)] =
    maybeSrcBase.map(srcBase => rebase(files, srcBase, destBase)).getOrElse(Seq.empty[(Path, String)])
}

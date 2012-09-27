import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import java.nio.file.Path
import sbt.Keys._
import sbt._
import Packaging._

object NativePackaging {
  implicit def p2f(path: Path) = path.toFile

  val defaultPackageSettings = Seq(
    // Linux
    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
    linux.Keys.packageSummary := "This is a summary of the package",
    linux.Keys.packageDescription := "This is the description of the package.",
    //    name := "wicket",
    linux.Keys.linuxPackageMappings in Linux <++= (
      pkgSrcHome, defaultsMapping, libMappings, confMappings, scriptMappings, launcherMapping, initdMapping, unixLogDir
      ) map (
      (home, etcDefault, libs, confs, scripts, launcher, initd, logDir) => Seq(
        pkgMaps(Seq(launcher, initd) ++ scripts, perms = "0755"),
        pkgMaps(libs),
        pkgMaps(confs :+ etcDefault, isConfig = true),
        pkgMap(home / "logs" -> logDir.toString, perms = "0755")
      )),
    // Debian
    debian.Keys.linuxPackageMappings in Debian <++= linux.Keys.linuxPackageMappings in Linux,
    debian.Keys.version := "0.1",
    debian.Keys.linuxPackageMappings in Debian <++= (pkgSrcHome, name, defaultsMapping,
      libMappings, confMappings, scriptMappings, launcherMapping, initdMapping,
      preInstall, postInstall, preRemove, postRemove) map (
      (home, pkgName, etcDefault, libs, confs, scripts, launcher, initd, preinst, postinst, prerm, postrm) => Seq(
        // http://lintian.debian.org/tags/no-copyright-file.html
        pkgMap((home / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright")),
        pkgMap((home / "copyright") -> ("/usr/share/doc/" + pkgName + "/changelog.gz"), gzipped = true) asDocs(),
        pkgMaps(Seq(
          preinst -> "DEBIAN/preinst",
          postinst -> "DEBIAN/postinst",
          prerm -> "DEBIAN/prerm",
          prerm -> "DEBIAN/postrm"
        ), perms = "0755")
      )),
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget"),
    // RPM
    rpm.Keys.linuxPackageMappings in Rpm <++= linux.Keys.linuxPackageMappings in Linux,
    rpm.Keys.rpmRelease := "0.1",
    rpm.Keys.rpmVendor := "kingmichael",
    rpm.Keys.rpmLicense := Some("You have the right to remain silent"),
    rpm.Keys.rpmPreInstall <<= (preInstall)(Some(_)),
    rpm.Keys.rpmPostInstall <<= (postInstall)(Some(_)),
    rpm.Keys.rpmPreRemove <<= (preRemove)(Some(_)),
    rpm.Keys.rpmPostRemove <<= (postRemove)(Some(_)),
    // Windows
    windows.Keys.wixFile := new File("doesnotexist")
  )

  def pkgMap(file: (Path, String), perms: String = "0644", gzipped: Boolean = false) =
    pkgMaps(Seq(file), perms = perms, gzipped = gzipped)

  def pkgMaps(files: Seq[(Path, String)],
              user: String = "root",
              group: String = "root",
              perms: String = "0644",
              isConfig: Boolean = false,
              gzipped: Boolean = false) = {
    var mapping = pkgMapping(files: _*) withUser user withGroup group withPerms perms
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
}

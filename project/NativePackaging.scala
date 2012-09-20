import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import java.nio.file.Path
import sbt.Keys._
import sbt._
import Packaging._
import scala.Some

object NativePackaging {
  val defaultPackageSettings = Seq(
    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
    linux.Keys.packageSummary := "This is a summary of the package",
    linux.Keys.packageDescription := "This is the description of the package.",
    //    name := "wicket",
    debian.Keys.version := "0.1",
    // Tag takes single token only
    rpm.Keys.rpmRelease := "0.1",
    rpm.Keys.rpmVendor := "kingmichael",
    rpm.Keys.rpmLicense := Some("You have the right to remain silent"),
    windows.Keys.wixFile := new File("doesnotexist"),
    debian.Keys.linuxPackageMappings in Debian <++= (pkgSrcHome, name, controlDir, defaultsMapping, libMappings, confMappings, scriptMappings, launcherMapping, initdMapping) map (
      // http://lintian.debian.org/tags/no-copyright-file.html
      (home, pkgName, control, etcDefault, libs, confs, scripts, launcher, initd) => Seq(
        pkgMap((home / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright")),
        pkgMap((home / "copyright") -> ("/usr/share/doc/" + pkgName + "/changelog.gz"), gzipped = true) asDocs(),
        pkgMaps(Seq(
          control / "preinstall.sh" -> "DEBIAN/preinst",
          control / "postinstall.sh" -> "DEBIAN/postinst",
          control / "preuninstall.sh" -> "DEBIAN/prerm",
          control / "postuninstall.sh" -> "DEBIAN/postrm",
          launcher,
          initd
        ) ++ scripts, perms = "0755"),
        pkgMaps(libs),
        pkgMaps(confs :+ etcDefault, isConfig = true)
      )),
    //    debian.Keys.linuxPackageMappings in Debian <+= (defaultsMapping, name) map (
    //      (defMap, pkgName) => (pkgMapping(defMap) withUser "root" withPerms "0755")
    //      ),
    //    debian.Keys.linuxPackageMappings in Debian <+= (libMappings, name) map (
    //      (defMap, pkgName) => (pkgMapping(defMap: _*) withUser "root" withPerms "0644")
    //      ),
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget")
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
  }
}

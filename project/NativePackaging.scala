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
    debian.Keys.linuxPackageMappings in Debian <++= (pkgSrcHome, name) map (
      // http://lintian.debian.org/tags/no-copyright-file.html
      (dir, pkgName) => Seq(
        (pkgMapping((dir / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright"))
          withUser "root" withPerms "0644"),
        (pkgMapping((dir / "copyright") -> ("/usr/share/doc/" + pkgName + "/changelog.gz"))
          withUser "root" withPerms "0644" gzipped) asDocs()
      )),
    debian.Keys.linuxPackageMappings in Debian <+= (controlDir, name) map (
      (dir, pkgName) => (pkgMapping(
        (dir / "preinstall.sh") -> "DEBIAN/preinst",
        (dir / "postinstall.sh") -> "DEBIAN/postinst",
        (dir / "preuninstall.sh") -> "DEBIAN/prerm",
        (dir / "postuninstall.sh") -> "DEBIAN/postrm"
      ) withUser "root" withPerms "0755")
      ),
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget")
  )

  def pkgMapping(files: (Path, String)*) = {
    packageMapping(files.map(pair => pair._1.toFile -> pair._2): _*)
  }
}

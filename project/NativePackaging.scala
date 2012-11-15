import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager._
import java.nio.file.Path
import linux.LinuxPackageMapping
import sbt.Keys._
import sbt._
import Packaging._
import Implicits._
import xml.NodeSeq

object NativePackaging {
  val linuxSettings: Seq[Setting[_]] = Seq(
    // Flat copy of libs to /lib on destination system
    libMappings <<= (libs, unixLibDest) map ((libFiles, destDir) => {
      libFiles.map(file => file -> (destDir / file.getFileName).toString)
    }),
    confMappings <<= (configFiles, configPath, unixConfDest) map rebase,
    scriptMappings <<= (scriptFiles, scriptPath, unixScriptDest) map rebase,
    confMappings <<= (configFiles, configPath, unixConfDest) map rebase,

    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
    linux.Keys.packageSummary := "This is a summary of the package",
    linux.Keys.packageDescription := "This is the description of the package.",
    //    name := "wicket",
    linux.Keys.linuxPackageMappings in Linux <++= (
      unixHome, unixPkgHome, name, appJar, libMappings, confMappings,
      scriptMappings, unixLogDir, appJarName
      ) map (
      (home, pkgSrc, pkgName, jarFile, libs, confs, scripts, logDir, jarName) => Seq(
        pkgMaps(Seq((pkgSrc / (pkgName + ".sh")) -> ("/etc/init.d/" + pkgName)) ++ scripts, perms = "0755"),
        pkgMaps(libs),
        pkgMaps(confs ++ Seq((pkgSrc / (pkgName + ".defaults")) -> ("/etc/default/" + pkgName)), isConfig = true),
        pkgMap((pkgSrc / "logs") -> logDir.toString, perms = "0755"),
        pkgMap(jarFile.toPath -> ((home / jarName).toString))
      ))
  )
  val debianSettings: Seq[Setting[_]] = Seq(
    debian.Keys.linuxPackageMappings in Debian <++= linux.Keys.linuxPackageMappings in Linux,
    debian.Keys.version := "0.1",
    debian.Keys.linuxPackageMappings in Debian <++= (unixPkgHome, name,
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
  val windowsMappings = mappings in windows.Keys.packageMsi in Windows
  val windowsSettings: Seq[Setting[_]] = Seq(
    windows.Keys.wixConfig <<= (windowsPkgHome, name, appJarName, libs) map (
      (winDir: Path, appName: String, jarName: String, libz: Seq[Path]) =>
        makeWindowsXml(winDir, appName, jarName, libz)
      ),
    //    windows.Keys.wixFile := new File("doesnotexist"),
    windowsMappings <+= (appJar, appJarName) map ((jar: Path, jarName: String) => jar.toFile -> jarName),
    windowsMappings <++= (libs) map ((libz: Seq[Path]) => libz.map(libPath => (libPath.toFile -> ("lib/" + libPath.getFileName.toString)))),
    windows.Keys.lightOptions ++= Seq("-ext", "WixUIExtension", "-cultures:en-us")
  )
  val defaultNativeProject: Seq[Setting[_]] = linuxSettings ++ debianSettings ++ rpmSettings ++ windowsSettings
  /**
   * Product GUID: AA8D2CDE-6274-4415-8DD4-0075BDE77FDA
   * Package GUID: C2726D33-268F-47EA-BDA8-1B21EC6CC5EE
   * Upgrade GUID: 5EC7F255-24F9-4E1C-B19D-581626C50F02
   * Launcher GUID: 24241F02-194C-4AAD-8BD4-379B26F1C661
   */
  /**
   *
   * @param windowsSrcDir
   * @return
   */
  def makeWindowsXml(windowsSrcDir: Path, appName: String, jarName: String, libz: Seq[Path]): scala.xml.Node = {
    val appVersion = "1.0.0"
    val windowsSrc = windowsSrcDir.toAbsolutePath.toString
    val wixXml = toWixFragment(libz)
    val libsXml = wixXml.compsFragment
    val compRefXml = wixXml.compRefs
    (<Wix xmlns='http://schemas.microsoft.com/wix/2006/wi' xmlns:util='http://schemas.microsoft.com/wix/UtilExtension'>
      <Product Name='Test app'
               Id='AA8D2CDE-6274-4415-8DD4-0075BDE77FDA'
               UpgradeCode='5EC7F255-24F9-4E1C-B19D-581626C50F02'
               Language='1033'
               Version={appVersion}
               Manufacturer='Skogberg Labs'>
        <Package Description='Test app launcher script.'
                 Comments='Test Windows installer.'
                 Manufacturer='Skogberg Labs'
                 InstallScope='perMachine'
                 InstallerVersion='200'
                 Compressed='yes'/>
        <Media Id='1' Cabinet='wicket.cab' EmbedCab='yes'/>
        <Directory Id='TARGETDIR' Name='SourceDir'>
          <Directory Id='ProgramFilesFolder' Name='PFiles'>
            <Directory Id='INSTALLDIR' Name='wicket'>
              <Directory Id='classes_dir' Name='classes'>
              </Directory>
              <Directory Id="lib_dir" Name="lib">
                {libsXml}
              </Directory>
              <Component Id='WicketLauncherScript' Guid='*'>
                <File Id='wicket_bat' Name='wicket.bat' DiskId='1' Source={windowsSrc + "\\" + appName + ".bat"}>
                  <!-- <util:PermissionEx User="Users" Domain="[LOCAL_MACHINE_NAME]" GenericRead="yes" Read="yes" GenericExecute="yes" ChangePermission="yes"/> -->
                </File>
              </Component>
              <Component Id='WicketLauncherJar' Guid='*'>
                <File Id='wicket_jar' Name='wicket.jar' DiskId='1' Source={jarName}/>
              </Component>
              <Component Id='WicketLauncherPath' Guid='24241F02-194C-4AAD-8BD4-379B26F1C661'>
                <CreateFolder/>
                <Environment Id="PATH" Name="PATH" Value="[INSTALLDIR]" Permanent="no" Part="last" Action="set" System="yes"/>
                <Environment Id="WICKET_HOME" Name="WICKET_HOME" Value="[INSTALLDIR]" Permanent="no" Action="set" System="yes"/>
              </Component>
            </Directory>
          </Directory>
        </Directory>
        <Feature Id='Complete'
                 Title='Wicket Application'
                 Description='The windows installation of wicket test.'
                 Display='expand'
                 Level='1'
                 ConfigurableDirectory='INSTALLDIR'>
          <Feature Id='WicketLauncher'
                   Title='Wicket Launcher Script'
                   Description='The application which and launches wicket test.'
                   Level='1'
                   Absent='disallow'>
            <ComponentRef Id='WicketLauncherScript'/>
            <ComponentRef Id='WicketLauncherJar'/>{compRefXml}
          </Feature>
          <Feature Id='WicketLauncherPath'
                   Title='Add wicket to windows system PATH'
                   Description='This will append wicket to your windows system path.'
                   Level='1'>
            <ComponentRef Id='WicketLauncherPath'/>
          </Feature>
        </Feature> <!--<Property Id="JAVAVERSION">
        <RegistrySearch Id="JavaVersion" Root="HKLM" Key="SOFTWARE\Javasoft\Java Runtime Environment" Name="CurrentVersion" Type="raw"/>
        </Property><Condition Message="This application requires a JVM available.  Please install Java, then run this installer again.">
        <![CDATA[Installed OR JAVAVERSION]]>      </Condition>-->
        <MajorUpgrade AllowDowngrades="no"
                      Schedule="afterInstallInitialize"
                      DowngradeErrorMessage="A later version of [ProductName] is already installed.  Setup will no exit."/>

        <UIRef Id="WixUI_FeatureTree"/>
        <UIRef Id="WixUI_ErrorProgressText"/>

        <Property Id="WIXUI_INSTALLDIR" Value="INSTALLDIR"/>
        <WixVariable Id="WixUILicenseRtf" Value={windowsSrc + "\\license.rtf"}/>
      </Product>
    </Wix>)
  }

  def toWixFragment(file: Path): WixFile = {
    val libPathString = file.toAbsolutePath.toString
    val fileName = file.getFileName.toString
    val fileId = fileName.replace('-', '_')
    val compId = fileId.filter(_ != '_')
    val fragment = (<Component Id={compId} Guid='*'>
      <File Id={fileId} Name={fileName} DiskId='1' Source={libPathString}>
      </File>
    </Component>)
    WixFile(compId, fragment)
  }

  def toCompRef(refId: String) = {
      <ComponentRef Id={refId}/>
  }

  def toWixFragment(files: Seq[Path]): WixFiles = {
    val wixFiles = files.map(toWixFragment)
    val compIds = wixFiles.map(_.compId)
    val compRefFragment = compIds.map(toCompRef).foldLeft(NodeSeq.Empty)(_ ++ _)
    val fragment = wixFiles.map(_.compFragment).foldLeft(NodeSeq.Empty)(_ ++ _)
    WixFiles(compIds, compRefFragment, fragment)
  }

  case class WixFiles(compIds: Seq[String], compRefs: NodeSeq, compsFragment: NodeSeq)

  case class WixFile(compId: String, compFragment: NodeSeq)

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

import java.nio.file.Path
import sbt.Plugin
import xml.NodeSeq

object WindowsPackaging extends Plugin{
  /**
   * Product GUID: AA8D2CDE-6274-4415-8DD4-0075BDE77FDA
   * Package GUID: C2726D33-268F-47EA-BDA8-1B21EC6CC5EE
   * Upgrade GUID: 5EC7F255-24F9-4E1C-B19D-581626C50F02
   * Launcher GUID: 24241F02-194C-4AAD-8BD4-379B26F1C661
   */
  /**
   *
   * @return
   */
  def makeWindowsXml(appName: String,
                     jarName: String,
                     libz: Seq[Path],
                     exe: Path,
                     bat:Path,
                      license:Path,
                      icon:Path,
                      winswExe:Path): scala.xml.Node = {
    val appVersion = "1.0.0"
    val wixXml = toWixFragment(libz)
    val libsXml = wixXml.compsFragment
    val compRefXml = wixXml.compRefs
    val exeFileName = exe.getFileName.toString
    val batFileName = bat.getFileName.toString
    val serviceExeName = appName+"svc.exe"
    (<Wix xmlns='http://schemas.microsoft.com/wix/2006/wi' xmlns:util='http://schemas.microsoft.com/wix/UtilExtension'>
      <Product Name={appName}
               Id='AA8D2CDE-6274-4415-8DD4-0075BDE77FDA'
               UpgradeCode='5EC7F255-24F9-4E1C-B19D-581626C50F02'
               Language='1033'
               Version={appVersion}
               Manufacturer='Skogberg Labs'>
        <Package Description={appName + " launcher script."}
                 Comments='Windows installer.'
                 Manufacturer='Skogberg Labs'
                 InstallScope='perMachine'
                 InstallerVersion='200'
                 Compressed='yes'/>
        <Media Id='1' Cabinet={appName+".cab"} EmbedCab='yes'/>
        <Icon Id={exeFileName} SourceFile={icon.toAbsolutePath.toString}/>
        <Property Id="ARPPRODUCTICON" Value={exeFileName} />
        <Directory Id='TARGETDIR' Name='SourceDir'>
          <Directory Id="DesktopFolder" Name="Desktop"/>
          <Directory Id='ProgramFilesFolder' Name='PFiles'>
            <Directory Id='INSTALLDIR' Name={appName}>
              <Directory Id='classes_dir' Name='classes'>
              </Directory>
              <Directory Id="lib_dir" Name="lib">
                {libsXml}
              </Directory>
              <Component Id='LauncherScript' Guid='*'>
                <File Id={batFileName.replace('.','_')} Name={batFileName} DiskId='1' Source={bat.toAbsolutePath.toString}>
                </File>
              </Component>
              <Component Id='LauncherJar' Guid='*'>
                <File Id={jarName.replace('.','_')} Name={jarName} DiskId='1' Source={jarName}/>
              </Component>
              <Component Id='ApplicationExecutable' Guid='*'>
                <File Id='app_exe' Name={exeFileName} DiskId='1' Source={exe.toAbsolutePath.toString} KeyPath="yes">
                  <Shortcut Id='desktopShortcut' Directory='DesktopFolder' Name={appName}
                            WorkingDirectory='INSTALLDIR' Icon={exeFileName} IconIndex="0" Advertise="yes" />
                </File>
              </Component>
              <Component Id='ServiceManager' Guid='*'>
                <File Id={serviceExeName.replace('.','_')} Name={appName+"svc.exe"} DiskId='1' Source={winswExe.toAbsolutePath.toString}>
                </File>
              </Component>
              <Component Id='AppLauncherPath' Guid='24241F02-194C-4AAD-8BD4-379B26F1C661'>
                <CreateFolder/>
                <Environment Id="PATH" Name="PATH" Value="[INSTALLDIR]" Permanent="no" Part="last" Action="set" System="yes"/>
                <Environment Id={appName.toUpperCase+"_HOME"} Name={appName.toUpperCase+"_HOME"} Value="[INSTALLDIR]" Permanent="no" Action="set" System="yes"/>
              </Component>
            </Directory>
          </Directory>
        </Directory>

        <Feature Id='Complete'
                 Title={appName + " application"}
                 Description={"The Windows installation of "+appName}
                 Display='expand'
                 Level='1'
                 ConfigurableDirectory='INSTALLDIR'>
          <Feature Id='CoreApp'
                   Title='Core Application'
                   Description='The core application.'
                   Level='1'
                   Absent='disallow'>
            <ComponentRef Id='LauncherScript'/>
            <ComponentRef Id='ApplicationExecutable'/>
            <ComponentRef Id='LauncherJar'/>{compRefXml}
          </Feature>
          <Feature Id='ConfigurePath'
                   Title={"Add "+appName+" to Windows system PATH"}
                   Description={"This will append "+appName+" to your Windows system path."}
                   Level='1'>
            <ComponentRef Id='AppLauncherPath'/>
          </Feature>
          <Feature Id='InstallAsService'
                   Title={"Install "+appName+" as a Windows service"}
                   Description={"This will install "+appName+" as a Windows service."}
                   Level='1'>
            <ComponentRef Id='ServiceManager'/>
          </Feature>
        </Feature>
        <MajorUpgrade AllowDowngrades="no"
                      Schedule="afterInstallInitialize"
                      DowngradeErrorMessage="A later version of [ProductName] is already installed.  Setup will no exit."/>

        <UIRef Id="WixUI_FeatureTree"/>
        <UIRef Id="WixUI_ErrorProgressText"/>

        <Property Id="WIXUI_INSTALLDIR" Value="INSTALLDIR"/>
        <WixVariable Id="WixUILicenseRtf" Value={license.toAbsolutePath.toString}/>
      </Product>
    </Wix>)
  }

  /**
                    <ServiceInstall Id="ServiceInstaller"
                                Type="ownProcess"
                                Vital="yes"
                                Name={appName}
                                DisplayName={appName+" service"}
                                Description={"The "+appName+" service"}
                                Start="auto"
                                Account="LocalSystem"
                                ErrorControl="ignore"
                                Interactive="no">
                </ServiceInstall>
                <ServiceControl Id="StartService" Start="install" Stop="both" Remove="uninstall" Name={appName} Wait="yes" />
   */
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
}
import java.nio.file.Path

object WindowsServiceWrapper{
  def init(appName:String,batFile:Path,winswExe:Path,homeVar:String){
    val conf =
    <service>
      <id>{appName}</id>
      <name>{appName} service</name>
      <description>{appName}</description>
      <executable>%{homeVar}%\{appName}.bat</executable>
      <logpath>C:\</logpath>
      <logmode>roll</logmode>
      <depend>Spooler</depend>
      <startargument>start</startargument>
      <stopargument>stop</stopargument>
    </service>
  }
  def wixFragment(winswExe:Path) = {
     <CustomAction Id="service_install"
                   Directory="INSTALLLOCATION"
                   ExeCommand={winswExe.toAbsolutePath.toString+" install"}
                   Execute="immediate"
                   Return="asyncNoWait"/>

  }
}
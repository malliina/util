package com.mle.util.security


/**
 *
 * @author mle
 */
trait DevelKeystoreSettings extends IKeystoreSettings {
  val keystorePass = "eternal"
  val truststore = "conf/security/ca-cert.jks"
  val truststorePass = "eternal"
}

object ClientKeystoreSettings extends DevelKeystoreSettings {
  val keystore = "conf/security/client.jks"
}

object ServerKeystoreSettings extends DevelKeystoreSettings {
  val keystore = "conf/security/server.jks"
}
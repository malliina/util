package com.mle.rmi

import com.mle.util.Util
import com.mle.util.security.KeystoreSettings

/**
 * The security policy and/or the [[java.lang.SecurityManager]]
 * must be installed BEFORE logback is used.
 *
 * @author Mle
 */
object RmiUtil {
  def initSecurity() {
    initMisc()
    initCerts()
  }

  def initCerts() {
    val keystore = "security/develkeys/keystore.key"
    val keySettings = KeystoreSettings(keystore, "changeme", keystore, "changeme")
    keySettings.prepareSystemProperties()
  }

  def initMisc() {
    sys.props("java.security.policy") = Util.resource("security/server.policy").toURI.toString
    //    if (System.getSecurityManager == null) {
    //      System.setSecurityManager(new SecurityManager)
    //    }
  }
}

package com.malliina.rmi

import com.malliina.security.{KeystoreSettings, MultiKeyStoreManager}
import com.malliina.util.Util

/**
 * The security policy and/or the [[java.lang.SecurityManager]]
 * must be installed BEFORE logback is used.
 *
 * @author Mle
 */
object RmiUtil {
  val keystore = "security/develkeys/keystore.key"
  val keySettings = KeystoreSettings(keystore, "changeme", keystore, "changeme")

  def initClientSecurity() {
    initSecurityPolicy()
    MultiKeyStoreManager.registerKeyStores(keySettings)
  }

  def initSecurityPolicy() {
    sys.props("java.security.policy") = Util.resource("security/server.policy").toURI.toString
    //    if (System.getSecurityManager == null) {
    //      System.setSecurityManager(new SecurityManager)
    //    }
  }
}

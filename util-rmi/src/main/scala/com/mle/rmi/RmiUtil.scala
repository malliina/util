package com.mle.rmi

import com.mle.util.{Util, FileUtilities}

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
    FileUtilities.resourceToFile(keystore).foreach(file =>
      println("Created " + file.toAbsolutePath + " for RMI")
    )
    sys.props("javax.net.ssl.keyStore") = FileUtilities.pathTo(keystore).toAbsolutePath.toString
    sys.props("javax.net.ssl.keyStorePassword") = "changeme"
    sys.props("javax.net.ssl.trustStore") = FileUtilities.pathTo(keystore).toAbsolutePath.toString
    sys.props("javax.net.ssl.trustStorePassword") = "changeme"
  }

  def initMisc() {
    sys.props("java.security.policy") = Util.resource("security/server.policy").toURI.toString
//    if (System.getSecurityManager == null) {
//      System.setSecurityManager(new SecurityManager)
//    }
  }
}

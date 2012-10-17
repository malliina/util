package com.mle.util.security

import com.mle.util.Util

/**
 *
 * @author mle
 */
class KeystoreSettings(keystore: String,
                       val keystorePass: String,
                       truststore: String,
                       val truststorePass: String) {
  val keystoreUrl = Util resource keystore
  val truststoreUrl = Util resource truststore

  def setSystemProperties() {
    sys.props("javax.net.ssl.trustStore") = truststoreUrl.getFile
    sys.props("javax.net.ssl.trustStorePassword") = truststorePass
    sys.props("javax.net.ssl.keyStore") = keystoreUrl.getFile
    sys.props("javax.net.ssl.keyStorePassword") = keystorePass
  }
}
package com.mle.util.security

import com.mle.util.Util

/**
 *
 * @author mle
 */
trait IKeystoreSettings {
  lazy val keystoreUrl = Util resource keystore
  lazy val truststoreUrl = Util resource truststore

  def keystore: String

  def keystorePass: String

  def truststore: String

  def truststorePass: String

  def setSystemProperties() {
    sys.props("javax.net.ssl.trustStore") = truststoreUrl.getFile
    sys.props("javax.net.ssl.trustStorePassword") = truststorePass
    sys.props("javax.net.ssl.keyStore") = keystoreUrl.getFile
    sys.props("javax.net.ssl.keyStorePassword") = keystorePass
  }
}

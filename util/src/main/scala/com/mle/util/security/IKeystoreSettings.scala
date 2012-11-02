package com.mle.util.security

import com.mle.util.{Log, FileUtilities, Util}

/**
 *
 * @author mle
 */
trait IKeystoreSettings extends Log {
  lazy val keystoreUrl = Util url keystore
  lazy val truststoreUrl = Util url truststore

  def keystore: String

  def keystorePass: String

  def truststore: String

  def truststorePass: String

  /**
   * Creates files of the keystore and truststore unless they already are such and sets the correct javax.ssl system properties.
   *
   * Files are needed because java's ssl tools do not accept resources from the classpath; only files.
   */
  def prepareSystemProperties() {
    val keystoreFileUrl = FileUtilities.resourceToFile(keystore).map(_.toUri.toURL)
      .getOrElse(keystoreUrl).getFile
    val truststoreFileUrl = FileUtilities.resourceToFile(truststore).map(_.toUri.toURL)
      .getOrElse(truststoreUrl).getFile
    sys.props("javax.net.ssl.trustStore") = truststoreFileUrl
    sys.props("javax.net.ssl.trustStorePassword") = truststorePass
    sys.props("javax.net.ssl.keyStore") = keystoreFileUrl
    sys.props("javax.net.ssl.keyStorePassword") = keystorePass
  }
}

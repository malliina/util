package com.mle.util.security

import com.mle.util.{Log, Util}

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
   * Registers the keys contained in this object with the JVM.
   *
   * @see [[com.mle.util.security.MultiKeyStoreManager]]
   *
   */
  def prepareSystemProperties() {
    MultiKeyStoreManager.addKeySettings(this)

    //    val keystoreFileUrl = FileUtilities.resourceToFile(keystore).map(_.toUri.toURL)
    //      .getOrElse(keystoreUrl).getFile
    //    val truststoreFileUrl = FileUtilities.resourceToFile(truststore).map(_.toUri.toURL)
    //      .getOrElse(truststoreUrl).getFile


    //    sys.props("javax.net.ssl.trustStore") = truststoreFileUrl
    //    sys.props("javax.net.ssl.trustStorePassword") = truststorePass
    //    sys.props("javax.net.ssl.keyStore") = keystoreFileUrl
    //    sys.props("javax.net.ssl.keyStorePassword") = keystorePass
  }
}

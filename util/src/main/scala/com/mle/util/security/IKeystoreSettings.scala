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
}

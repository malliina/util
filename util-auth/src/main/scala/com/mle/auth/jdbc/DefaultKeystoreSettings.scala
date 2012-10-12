package com.mle.auth.jdbc

/**
 *
 * @author mle
 */
object DefaultKeystoreSettings extends KeystoreSettings(
  keystore = "conf/security/client.jks",
  keystorePass = "eternal",
  truststore = "conf/security/ca-cert.jks",
  truststorePass = "eternal")
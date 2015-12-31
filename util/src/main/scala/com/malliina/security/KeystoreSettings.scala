package com.malliina.security


/**
 *
 * @author mle
 */
case class KeystoreSettings(keystore: String,
                            keystorePass: String,
                            truststore: String,
                            truststorePass: String) extends IKeystoreSettings
package com.mle.util.security


/**
 *
 * @author mle
 */
case class KeystoreSettings(keystore: String,
                            keystorePass: String,
                            truststore: String,
                            truststorePass: String) extends IKeystoreSettings
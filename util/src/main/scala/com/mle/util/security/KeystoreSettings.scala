package com.mle.util.security


/**
 *
 * @author mle
 */
class KeystoreSettings(val keystore: String,
                       val keystorePass: String,
                       val truststore: String,
                       val truststorePass: String) extends IKeystoreSettings
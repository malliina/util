package com.mle.auth.jdbc

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
}
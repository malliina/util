package com.malliina.security


case class KeystoreSettings(keystore: String,
                            keystorePass: String,
                            truststore: String,
                            truststorePass: String) extends IKeystoreSettings

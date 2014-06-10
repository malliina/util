package com.mle.security

import com.mle.util.Util
import java.nio.file.{Path, Paths}
import java.security.KeyStore
import java.io.FileInputStream
import com.mle.exception.ConfigException
import com.mle.file.FileUtilities

/**
 *
 * @author mle
 */
trait KeyStores {
  val (keyStoreKey, keyStorePassKey, keyStoreTypeKey) =
    ("https.keyStore", "https.keyStorePassword", "https.keyStoreType")
  val defaultKeyStoreType = "JKS"

  def validateKeyStoreIfSpecified(): Unit = {
    sysProp(keyStoreKey) foreach (keyStore => {
      val absPath = FileUtilities.pathTo(keyStore).toAbsolutePath
      FileUtilities.verifyFileReadability(absPath)
      val pass = sysProp(keyStorePassKey) getOrElse (throw new ConfigException(s"Key $keyStoreKey exists but no corresponding $keyStorePassKey was found."))
      val storeType = sysProp(keyStoreTypeKey) getOrElse defaultKeyStoreType
      validateKeyStore(Paths get keyStore, pass, storeType)
    })
  }

  def validateKeyStore(keyStore: Path, keyStorePassword: String, keyStoreType: String = defaultKeyStoreType): Unit = {
    val ks = KeyStore.getInstance(keyStoreType)
    Util.using(new FileInputStream(keyStore.toFile))(keyStream => ks.load(keyStream, keyStorePassword.toCharArray))
  }

  private def sysProp(key: String) = sys.props.get(key)
}

object KeyStores extends KeyStores
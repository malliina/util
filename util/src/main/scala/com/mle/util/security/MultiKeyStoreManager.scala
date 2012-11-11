package com.mle.util.security

import java.net.Socket
import com.mle.util.Log
import java.security.{NoSuchAlgorithmException, KeyStore, PrivateKey, Principal}
import java.security.cert.X509Certificate
import javax.net.ssl._
import com.mle.util.Util._

/**
 * Manages multiple different keystores/truststores for one JVM instance.
 *
 * Usage: <code>MultiKeyStoreManager.addKeySettings(IKeystoreSettings)</code> instead of:
 *
 * <code>sys.props("javax.net.ssl.keyStore") = keystorePath<code> etc
 *
 * @see http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm
 *
 * @author mle
 */

class MultiKeyStoreManager(managers: Seq[X509KeyManager])
  extends X509KeyManager with Log {
  log info "Initializing multi-keystore manager with: " + managers.size + " keystore managers"

  def chooseClientAlias(keyType: Array[String], issuers: Array[Principal], socket: Socket): String = {
    managers.map(_.chooseClientAlias(keyType, issuers, socket))
      .find(_ != null).orNull
  }

  def chooseServerAlias(keyType: String, issuers: Array[Principal], socket: Socket): String = {
    managers.map(_.chooseServerAlias(keyType, issuers, socket))
      .find(_ != null).orNull
  }

  def getCertificateChain(alias: String): Array[X509Certificate] = {
    managers.map(_.getCertificateChain(alias))
      .find(chain => chain != null && chain.size > 0).orNull
  }

  def getClientAliases(keyType: String, issuers: Array[Principal]) = {
    managers.map(_.getClientAliases(keyType, issuers)).flatten.toArray
  }

  def getServerAliases(keyType: String, issuers: Array[Principal]) = {
    managers.map(_.getServerAliases(keyType, issuers)).flatten.toArray
  }

  def getPrivateKey(alias: String): PrivateKey = {
    managers.map(_.getPrivateKey(alias))
      .find(_ != null).orNull
  }
}

object MultiKeyStoreManager extends Log {
  private var keySettingsList: List[IKeystoreSettings] = Nil

  /**
   * Adds the given key settings to the SSL context.
   *
   * Applications that wish to set SSL properties like javax.net.ssl.* can - instead
   * of polluting system properties - register the key settings using this method.
   *
   * @param keySettings keystore/truststore info to add to the JVM
   */
  def addKeySettings(keySettings: IKeystoreSettings) {
    keySettingsList = keySettings :: keySettingsList
    initManager(keySettingsList)
  }

  /**
   * Reconfigures the default SSL context with the given keystore settings.
   *
   * @param keySettings keystore/truststore details
   */
  private def initManager(keySettings: List[IKeystoreSettings]) {
    val keyManagers = keySettings.map(ks => buildKeyManager(ks.keystore, ks.keystorePass)) ::: buildJvmKeyManager :: Nil
    val trustManagers = keySettings.map(ks => buildTrustManager(ks.truststore, ks.truststorePass)) ::: buildJvmTrustManager :: Nil
    // construct and initialise a SSLContext with the KeyStore and TrustStore. We use the default SecureRandom.
    val context = SSLContext.getInstance("SSL")
    context.init(keyManagers.toArray, trustManagers.toArray, null)
    SSLContext.setDefault(context)
    log info "Changed default SSL context"
  }

  def loadKeyStore(storePath: String, storePass: String) = using(uri(storePath).toURL.openStream())(fis => {
    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(fis, storePass.toCharArray)
    log info "Loaded keystore: " + storePath
    keyStore
  })

  def getKeyManagers(keystorePath: String, keystorePass: String): Array[KeyManager] = {
    val customManager = buildKeyManager(keystorePath, keystorePass)
    val jvmManager = buildJvmKeyManager
    val ret: Array[KeyManager] = Array(new MultiKeyStoreManager(Seq(customManager, jvmManager)))
    ret
  }

  def buildKeyManager(keyStorePath: String, keyStorePass: String) = {
    val alg = KeyManagerFactory.getDefaultAlgorithm
    val myKeyManagerFactory = KeyManagerFactory.getInstance(alg)
    val ks = loadKeyStore(keyStorePath, keyStorePass)
    myKeyManagerFactory.init(ks, keyStorePass.toCharArray)
    getX509KeyManager(alg, myKeyManagerFactory)
  }

  def buildJvmKeyManager = {
    val alg = KeyManagerFactory.getDefaultAlgorithm
    val defaultKeyManagerFactory = KeyManagerFactory.getInstance(alg)
    defaultKeyManagerFactory.init(null, null)
    getX509KeyManager(alg, defaultKeyManagerFactory)
  }

  def getTrustManagers(trustStorePath: String, trustStorePass: String) = {
    val ret: Array[TrustManager] = Array(buildTrustManager(trustStorePath, trustStorePass), buildJvmTrustManager)
    ret
  }

  def buildTrustManager(trustStorePath: String, trustStorePass: String) = {
    val alg = TrustManagerFactory.getDefaultAlgorithm
    val trustManagerFactory = TrustManagerFactory.getInstance(alg)
    val ks = loadKeyStore(trustStorePath, trustStorePass)
    trustManagerFactory.init(ks)
    getX509TrustManager(alg, trustManagerFactory)
  }

  def buildJvmTrustManager = {
    val alg = TrustManagerFactory.getDefaultAlgorithm
    val trustManagerFactory = TrustManagerFactory.getInstance(alg)
    trustManagerFactory.init(null.asInstanceOf[KeyStore])
    getX509TrustManager(alg, trustManagerFactory)
  }


  /**
   * Find a X509 Key Manager compatible with a particular algorithm
   * @param algorithm
   * @param kmFact
   * @return
   * @throws NoSuchAlgorithmException
   */
  def getX509KeyManager(algorithm: String, kmFact: KeyManagerFactory): X509KeyManager = {
    val keyManagers = kmFact.getKeyManagers
    if (keyManagers == null || keyManagers.length == 0) {
      throw new NoSuchAlgorithmException("The default algorithm: " + algorithm + " produced no key managers")
    }
    keyManagers.find(_.isInstanceOf[X509KeyManager])
      .map(_.asInstanceOf[X509KeyManager])
      .getOrElse(throw new NoSuchAlgorithmException("The default algorithm: " + algorithm + " did not produce a X509 key manager"))
  }

  def getX509TrustManager(algorithm: String, kmFact: TrustManagerFactory): X509TrustManager = {
    val trustManagers = kmFact.getTrustManagers
    if (trustManagers == null || trustManagers.length == 0) {
      throw new NoSuchAlgorithmException("The default algorithm: " + algorithm + " produced no trust managers")
    }
    trustManagers.find(_.isInstanceOf[X509TrustManager])
      .map(_.asInstanceOf[X509TrustManager])
      .getOrElse(throw new NoSuchAlgorithmException("The default algorithm: " + algorithm + " did not produce a X509 trust manager"))
  }
}

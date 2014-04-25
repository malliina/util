package com.mle.security

import com.mle.util.Log
import java.security.{NoSuchAlgorithmException, KeyStore}
import javax.net.ssl._
import com.mle.util.Util._

/**
 * Manages multiple different keystores/truststores for one JVM instance.
 *
 * Removes the reliance on system properties to get SSL to work.
 *
 * Usage: <code>MultiKeyStoreManager.registerKeyStores(IKeystoreSettings)</code> instead of:
 *
 * <code>sys.props("javax.net.ssl.keyStore") = keystorePath<code> etc
 *
 * This class is not thread-safe.
 *
 * Note: Modules registering key/truststores using this object will trust all other truststores,
 * in particular also truststores registered by other modules.
 * Therefore, registering keystores like this is not recommended in multi-module applications unless no other option exists,
 * since module A may inadvertedly trust a truststore registered by module B, which is hardly ever desired.
 * A "module" is in this context something that uses truststores/keystores.
 *
 * @see http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm for some early inspiration
 *
 * @author mle
 */
object MultiKeyStoreManager extends Log {
  private var registeredKeyStores: List[IKeystoreSettings] = Nil

  /**
   * Adds the given key settings to the SSL context.
   *
   * Applications that wish to set SSL properties like javax.net.ssl.* can - instead
   * of polluting system properties - register the key settings using this method.
   *
   * @param keySettings keystore/truststore info to add to the JVM
   */
  def registerKeyStores(keySettings: IKeystoreSettings) {
    registeredKeyStores = keySettings :: registeredKeyStores
    reconfigureDefaultSslContext(registeredKeyStores)
  }

  /**
   * Reconfigures the default SSL context with the given keystore settings.
   *
   * @param keySettings keystore/truststore details
   */
  private def reconfigureDefaultSslContext(keySettings: List[IKeystoreSettings]) {
    val keyManagers = keySettings.map(ks => newKeyManager(ks.keystore, ks.keystorePass)) ::: newJvmKeyManager :: Nil
    val trustManagers = keySettings.map(ks => newTrustManager(ks.truststore, ks.truststorePass)) ::: newJvmTrustManager :: Nil
    // construct and initialise a SSLContext with the KeyStore and TrustStore. We use the default SecureRandom.
    val context = SSLContext.getInstance("SSL")
    context.init(keyManagers.toArray, trustManagers.toArray, null)
    SSLContext.setDefault(context)
    log info "Changed default SSL context"
  }

  /**
   * Constructs an SSL context with the given key settings.
   *
   * The context will be initialized with a key/trust manager built from the given key settings in addition to the default JVM key/trust manager.
   *
   * @param keySettings key settings to initialize context with
   * @return a new SSL context
   */
  def newSslContext(keySettings: IKeystoreSettings) = {
    val keyManagers = newKeyManager(keySettings.keystore, keySettings.keystorePass) :: newJvmKeyManager :: Nil
    val trustManagers = newTrustManager(keySettings.truststore, keySettings.truststorePass) :: newJvmTrustManager :: Nil
    val context = SSLContext.getInstance("TLS")
    context init(keyManagers.toArray, trustManagers.toArray, null)
    context
  }

  def toJksKeyStore(storePath: String, storePass: String) = using(uri(storePath).toURL.openStream())(fis => {
    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(fis, storePass.toCharArray)
    log info "Loaded keystore: " + storePath
    keyStore
  })

  def newKeyManager(keyStorePath: String, keyStorePass: String): X509KeyManager = {
    val ks = toJksKeyStore(keyStorePath, keyStorePass)
    newKeyManager(Some(ks), Some(keyStorePass))
  }

  def newJvmKeyManager = newKeyManager(None, None)

  /**
   * Build a key manager from a keystore.
   *
   * Pass None as arguments in order to get the default JVM key manager.
   *
   * @param keyStore the keystore with which to initialize the keystore manager
   * @param keyStorePass
   * @return a key manager
   */
  def newKeyManager(keyStore: Option[KeyStore], keyStorePass: Option[String]) = {
    val alg = KeyManagerFactory.getDefaultAlgorithm
    val kmf = KeyManagerFactory.getInstance(alg)
    kmf.init(keyStore.orNull, keyStorePass.map(_.toCharArray).orNull)
    getX509KeyManager(alg, kmf)
  }

  def newTrustManager(trustStorePath: String, trustStorePass: String): X509TrustManager = {
    val ks = toJksKeyStore(trustStorePath, trustStorePass)
    newTrustManager(Some(ks))
  }

  def newJvmTrustManager = newTrustManager(None)

  /**
   * Pass None parameter to get the default JVM trust manager.
   * @param keyStore the truststore with which to initialize the trust manager
   * @return
   */
  def newTrustManager(keyStore: Option[KeyStore]) = {
    val alg = TrustManagerFactory.getDefaultAlgorithm
    val trustManagerFactory = TrustManagerFactory.getInstance(alg)
    // method init is overloaded; the null-cast directs us to the method taking a KeyStore as parameter
    trustManagerFactory.init(keyStore.getOrElse(null.asInstanceOf[KeyStore]))
    getX509TrustManager(alg, trustManagerFactory)
  }

  /**
   * Find a X509 key manager compatible with a particular algorithm
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

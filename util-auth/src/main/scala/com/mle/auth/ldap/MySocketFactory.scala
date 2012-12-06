package com.mle.auth.ldap

import javax.net.SocketFactory
import com.mle.util.security.{IKeystoreSettings, ClientKeystoreSettings, MultiKeyStoreManager}
import java.net.{Socket, InetAddress}
import javax.net.ssl.SSLSocketFactory

/**
 * A socket factory based on custom keystore settings.
 *
 * Builds an SSL context initialized with the given keystores/truststores.
 *
 * All implemented methods just delegate to the factory of this custom SSL context.
 *
 * Used for libraries that accept a custom [[javax.net.SocketFactory]] for cases where the user wishes to use custom keystores.
 *
 * This class and any subclasses must implement getDefault() in its companion object (static in Java)
 * for compatibility with JNDI LDAP and maybe other libraries.
 *
 * @author mle
 */
class MySocketFactory(keySettings: IKeystoreSettings) extends SSLSocketFactory {
  private val context = MultiKeyStoreManager.newSslContext(keySettings)
  val socketFactory = context.getSocketFactory

  def createSocket(p1: String, p2: Int) = socketFactory.createSocket(p1, p2)

  def createSocket(p1: String, p2: Int, p3: InetAddress, p4: Int) = socketFactory.createSocket(p1, p2, p3, p4)

  def createSocket(p1: InetAddress, p2: Int) = socketFactory.createSocket(p1, p2)

  def createSocket(p1: InetAddress, p2: Int, p3: InetAddress, p4: Int) = socketFactory.createSocket(p1, p2, p3, p4)

  def getDefaultCipherSuites = socketFactory.getDefaultCipherSuites

  def getSupportedCipherSuites = socketFactory.getSupportedCipherSuites

  def createSocket(p1: Socket, p2: String, p3: Int, p4: Boolean) = socketFactory.createSocket(p1, p2, p3, p4)

  /**
   * The default implementation must not be used as we want to delegate to socketFactory.
   *
   * @return
   */
  override def createSocket() = socketFactory.createSocket()
}

object MySocketFactory {
  val socketFactory = new MySocketFactory(ClientKeystoreSettings)
  // do not remove. need a "static" getDefault method for compat with java jndi
  val getDefault: SocketFactory = socketFactory
}
package com.mle.rmi

import java.net.InetAddress
import javax.net.ssl.{SSLContext, SSLServerSocket}
import javax.rmi.ssl.SslRMIServerSocketFactory

/**
 * To use specific keystores/truststores, provide your own [[javax.net.ssl.SSLContext]] initialized with the stores.
 *
 * @author Mle
 */
class PickyServerSocketFactory(listenAddress: String = "127.0.0.1", clientAuth: Boolean = true, context: SSLContext)
  extends SslRMIServerSocketFactory(context, null, null, clientAuth) {
  /**
   * Overridden to force the server socket address to the one given in the class of this constructor.
   *
   * @param port listen port
   * @return a socket
   */
  override def createServerSocket(port: Int) = {
    val factory = context.getServerSocketFactory
    // SSLServerSocketFactory.getDefault.asInstanceOf[SSLServerSocketFactory]
    val socket = factory.createServerSocket(port, 0, InetAddress getByName listenAddress).asInstanceOf[SSLServerSocket]
    socket setNeedClientAuth clientAuth
    socket setUseClientMode false
    Option(getEnabledProtocols).foreach(protocols => socket setEnabledProtocols protocols)
    Option(getEnabledCipherSuites).foreach(ciphers => socket setEnabledCipherSuites ciphers)
    socket
  }
}

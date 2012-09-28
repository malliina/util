package com.mle.rmi

import java.net.InetAddress
import javax.net.ssl.{SSLServerSocket, SSLServerSocketFactory}
import javax.rmi.ssl.SslRMIServerSocketFactory

/**
 * @author Mle
 */
class PickyServerSocketFactory(listenAddress: String, clientAuth: Boolean = true) extends SslRMIServerSocketFactory(null, null, clientAuth) {
  def this() = this("127.0.0.1")

  override def createServerSocket(port: Int) = {
    val factory = SSLServerSocketFactory.getDefault.asInstanceOf[SSLServerSocketFactory]
    val socket = factory.createServerSocket(port, 0, InetAddress getByName listenAddress).asInstanceOf[SSLServerSocket]
    socket setNeedClientAuth clientAuth
    socket setUseClientMode false
    Option(getEnabledProtocols).foreach(protocols => socket setEnabledProtocols protocols)
    Option(getEnabledCipherSuites).foreach(ciphers => socket setEnabledCipherSuites ciphers)
    socket
  }
}

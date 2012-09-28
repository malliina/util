package com.mle.rmi

import javax.rmi.ssl.SslRMIClientSocketFactory

/**
 * @author Mle
 */
class PickyClientSocketFactory(ip: String) extends SslRMIClientSocketFactory {
  def this() = this("127.0.0.1")

  /**
   * Creates a socket to the host given to this factory during instantiation. Ignores the host parameter.
   * @param host ignored
   * @param port
   * @return socket to ip:port
   */
  override def createSocket(host: String, port: Int) = super.createSocket(ip, port)
}

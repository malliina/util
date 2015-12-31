package com.malliina.rmi

import com.malliina.util.Log
import java.rmi.registry.LocateRegistry
import java.rmi.Remote
import javax.net.ssl.SSLContext

/**
 * @author Mle
 */
object RmiRegistry extends Log {
  val DEFAULT_PORT = 2666

  /**
   *
   * @param port registry port
   * @param stubs the remote interfaces to bind, along with their reference names
   * @tparam T type of interface
   * @return a registry
   */
  def init[T <: Remote](port: Int, context: SSLContext)(stubs: (String, T)*) = {
    val registry = LocateRegistry.createRegistry(port, new PickyClientSocketFactory, new PickyServerSocketFactory(context = context))
    stubs.foreach(stub => {
      val (referenceName, obj) = stub
      registry.rebind(referenceName, obj)
    })
    val boundNames = registry.list()
    log info "Created local RMI registry on port: " + port + ", bound interfaces: " + boundNames.mkString(", ")
    registry
  }
}

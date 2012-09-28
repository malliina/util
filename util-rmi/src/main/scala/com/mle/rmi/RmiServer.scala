package com.mle.rmi

import com.mle.util.Log
import java.rmi.server.UnicastRemoteObject
import java.io.Closeable
import java.rmi.NoSuchObjectException

/**
 * Create a keystore with: keytool -genkey -alias rmi -keyalg RSA -validity 9999 -keystore keystore.key
 *
 * @author Mle
 */
class RmiServer(port: Int = RmiRegistry.DEFAULT_PORT) extends Closeable with Log {
  val rmiImpl = new RmiImpl(this)
  val stub = UnicastRemoteObject.exportObject(
    rmiImpl,
    0,
    new PickyClientSocketFactory,
    new PickyServerSocketFactory
  ).asInstanceOf[RmiInterface]
  val registry = RmiRegistry.init(port)(stubs = classOf[RmiInterface].getSimpleName -> stub)
  val remoteObjects = Seq(rmiImpl, registry)
  // Wait for registry daemon thread to start
  Thread sleep 100

  def close() {
    remoteObjects.foreach(remoteObj => try {
      UnicastRemoteObject.unexportObject(remoteObj, true)
    } catch {
      case e: NoSuchObjectException => log warn("Attempted to unexport object that wasn't exported", e)
    })
    registry.list().foreach(registry.unbind(_))
    log info "The RMI server has shut down"
  }
}

object RmiServer {
  def start = {
    RmiUtil.initSecurity()
    new RmiServer()
  }
}

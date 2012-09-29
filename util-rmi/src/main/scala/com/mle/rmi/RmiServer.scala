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
  val remoteObject = new RmiImpl(this)
  val stub = toStub(remoteObject)
  val registry = RmiRegistry.init(port)(classOf[RmiInterface].getSimpleName -> stub)
  registry.bind(remoteObject.getClass.getSimpleName, remoteObject)

  // Wait for registry daemon thread to start
  Thread sleep 100

  private def toStub(impl: RmiInterface): RmiInterface = UnicastRemoteObject.exportObject(
    impl,
    0,
    new PickyClientSocketFactory,
    new PickyServerSocketFactory
  ).asInstanceOf[RmiInterface]

  def close() {
    UnicastRemoteObject.unexportObject(registry, true)
    try {
      UnicastRemoteObject.unexportObject(remoteObject, true)
    } catch {
      case e: NoSuchObjectException => log warn("Attempted to unexport object that wasn't exported", e)
    }
    registry.list().foreach(registry.unbind(_))
    onClosed()
    log info "The RMI server has shut down"
  }

  def onClosed() {}
}

object RmiServer {
  def start = {
    RmiUtil.initSecurity()
    new RmiServer()
  }
}

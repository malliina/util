package com.malliina.rmi

import java.io.Closeable
import java.rmi.NoSuchObjectException
import java.rmi.server.UnicastRemoteObject

import com.malliina.security.{IKeystoreSettings, MultiKeyStoreManager}
import com.malliina.util.Log

/** Create a keystore with: keytool -genkey -alias rmi -keyalg RSA -validity 9999 -keystore keystore.key
  */
class RmiServer(registryPort: Int = RmiRegistry.DEFAULT_PORT,
                keySettings: IKeystoreSettings = RmiUtil.keySettings)
  extends Closeable with Log {
  private val sslContext = MultiKeyStoreManager.newSslContext(keySettings)
  val remoteObject = new RmiImpl(this)
  val stub = toStub(remoteObject)
  val registry = RmiRegistry.init(registryPort, sslContext)(classOf[RmiInterface].getSimpleName -> stub)
  registry.bind(remoteObject.getClass.getSimpleName, remoteObject)
  // Wait for registry daemon thread to start
  Thread sleep 100

  private def toStub(impl: RmiInterface): RmiInterface = UnicastRemoteObject.exportObject(
    impl,
    0,
    new PickyClientSocketFactory,
    new PickyServerSocketFactory(context = sslContext)
  ).asInstanceOf[RmiInterface]

  def close(): Unit = {
    UnicastRemoteObject.unexportObject(registry, true)
    try {
      UnicastRemoteObject.unexportObject(remoteObject, true)
    } catch {
      case e: NoSuchObjectException => log warn("Attempted to unexport object that wasn't exported", e)
    }
    registry.list().foreach(registry.unbind)
    onClosed()
    log info "The RMI server has shut down"
  }

  def onClosed(): Unit = {}
}

object RmiServer {
  def start(keySettings: IKeystoreSettings) = {
    RmiUtil.initSecurityPolicy()
    new RmiServer(keySettings = keySettings)
  }
}

package com.malliina.rmi

import java.rmi.{ConnectException, ConnectIOException}

import com.malliina.util.Log
import java.rmi.registry.LocateRegistry

/**
 * @author Mle
 *
 * @throws ConnectException check that the registry is running
 * @throws ConnectIOException check SSL settings
 */
class RmiClient extends Log {
  val registry = LocateRegistry.getRegistry("localhost", RmiRegistry.DEFAULT_PORT, new PickyClientSocketFactory)
  val intf = registry.lookup(classOf[RmiInterface].getSimpleName).asInstanceOf[RmiInterface]
  intf.shutdown()
  log info "I, client, shutdown the server. Bye."
}

/**
 * Used to stop the server over RMI.
 *
 * The server is assumed to have been started elsewhere using <code>RmiServer.start()</code>.
 *
 * Init scripts can for example use something like "java -jar app.jar com.malliina.rmi.RmiClient stop" to stop the server.
 */
object RmiClient {
  RmiUtil.initClientSecurity()

  def launchClient() {
    new RmiClient
  }

  def main(args: Array[String]) {
    if (args.size < 1)
      throw new Exception("Please specify the command as the first parameter")
    val command = args(0)
    command match {
      case "start" =>
        new RmiServer(keySettings = RmiUtil.keySettings)
      case "stop" =>
        launchClient()
    }
  }
}
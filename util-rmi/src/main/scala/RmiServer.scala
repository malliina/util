import com.mle.util.{Util, Log}
import java.rmi.NoSuchObjectException
import java.rmi.server.UnicastRemoteObject

/**
 * Create a keystore with: keytool -genkey -alias rmi -keyalg RSA  -validity 9999 -keystore keystore.key
 *
 * @author Mle
 */
class RmiServer(port: Int = 2666) extends Log {
  if (System.getSecurityManager == null)
    System.setSecurityManager(new SecurityManager)
  sys.props("java.security.policy") = Util.resource("security/client.policy").toString
  val stub = UnicastRemoteObject.exportObject(
    new RmiImpl,
    0,
    new PickyClientSocketFactory,
    new PickyServerSocketFactory
  ).asInstanceOf[RmiInterface]
  val registry = RmiRegistry.init(port, stub)

  def close() {
    try {
      UnicastRemoteObject.unexportObject(stub, true)
    } catch {
      case e: NoSuchObjectException => log warn "Attempted to unexport object that wasn't exported"
    }
    registry.list().foreach(registry.unbind(_))
    log info "The RMI server has shut down"
  }

}

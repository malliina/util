import com.mle.util.Log
import java.rmi.registry.LocateRegistry
import java.rmi.Remote

/**
 * @author Mle
 */
object RmiRegistry extends Log {
  /**
   *
   * @param port registry port
   * @param stub the remote interface to bind
   * @tparam T type of interface
   * @return a registry
   */
  def init[T <: Remote](port: Int, stub: T) = {
    val registry = LocateRegistry.createRegistry(port, new PickyClientSocketFactory, new PickyServerSocketFactory)
    val referenceName = classOf[T].getSimpleName
    registry.rebind(referenceName, stub)
    registry.list()
    log info "Created local RMI registry on port: " + port
    registry
  }
}

import com.mle.util.Log

/**
 * @author Mle
 */
class RmiImpl extends RmiInterface with Log {
  @scala.remote
  def shutdown() {
    log info "Bye"
  }
}

package com.mle.rmi

import com.mle.util.Log
import java.io.Closeable

/**
 * @author Mle
 */
class RmiImpl(server: Closeable) extends RmiInterface with Log {
  @scala.remote
  def shutdown() {
    server.close()
  }
}

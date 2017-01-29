package com.malliina.rmi

import com.malliina.util.Log
import java.io.Closeable

class RmiImpl(server: Closeable) extends RmiInterface with Log {
  @scala.remote
  def shutdown() {
    server.close()
  }
}

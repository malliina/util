package com.malliina.rmi

import java.rmi.Remote

/**
 * @author Mle
 */
trait RmiInterface extends Remote {
  @scala.remote
  def shutdown()
}

package com.malliina.rmi

import java.rmi.Remote

trait RmiInterface extends Remote {
  @scala.remote
  def shutdown()
}

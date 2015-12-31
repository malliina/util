package com.malliina.auth.crypto

import org.apache.commons.codec.digest.DigestUtils

/**
 *
 * @author mle
 */
trait Hashing {
  def hash(username: String, password: String) = Hashing.MD5(username + ":" + password)
}

object Hashing {
  def MD5(clearText: String) = DigestUtils.md5Hex(clearText)
//  def SSHA(clearText:String)=DigestUtils.sh
}

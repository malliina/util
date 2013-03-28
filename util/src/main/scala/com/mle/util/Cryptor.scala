package com.mle.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Hex

/**
 * @author Michael
 */
class Cryptor(hexKey: Array[Char]) {
  val algorithm = "AES"
  private val keySpec = new SecretKeySpec(Hex.decodeHex(hexKey), algorithm)

  def encrypt(input: Array[Byte]) = {
    val cipher = Cipher getInstance algorithm
    cipher init(Cipher.ENCRYPT_MODE, keySpec)
    cipher doFinal input
  }

  def encrypt(input: String): Array[Byte] = encrypt(input.getBytes)

  def encryptToHex(input: String) = new String(Hex.encodeHex(encrypt(input)))

  def decrypt(input: Array[Byte]) = {
    val cipher = Cipher getInstance algorithm
    cipher init(Cipher.DECRYPT_MODE, keySpec)
    cipher doFinal input
  }

  /**
   * Don't use this method unless you know you're decrypting character input.
   * @param input encrypted input
   * @return decrypted output, as a string!
   */
  def decryptToString(input: Array[Byte]) = new String(decrypt(input))

  def decryptFromHex(encryptedHexInput: String) = decryptToString(Hex.decodeHex(encryptedHexInput.toCharArray))
}


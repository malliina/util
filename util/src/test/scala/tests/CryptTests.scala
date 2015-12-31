package tests

import com.malliina.util.{Cryptor, Log}
import org.scalatest.FunSuite

/**
 * @author Michael
 */
class CryptTests extends FunSuite with Log {
  val cleartext = "hello, world!"

  object TestCryptor extends Cryptor(hexKey = "7311ddeb3b4b9e810ec5539a3b72d1b2".toCharArray)

  import TestCryptor._

  test("encrypted output does not equal cleartext") {
    assert(cleartext != encryptToHex(cleartext))
  }
  test("decrypt(encrypt(x)) == x") {
    val encrypted = encryptToHex(cleartext)
    val decrypted = decryptFromHex(encrypted)
    assert(cleartext === decrypted)
  }
  test("encryption is a pseudo-pure function") {
    val enc1 = encryptToHex(cleartext)
    val enc2 = encryptToHex(cleartext)
    assert(enc1 === enc2)
  }

}

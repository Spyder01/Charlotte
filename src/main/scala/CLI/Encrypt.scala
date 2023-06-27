package CLI

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

case class Encrypt(key: String) {
  private val initVector = "encryptionIntVec";

  private def hashToFixedLength (text: String): String = {
    return text
  }
  def encrypt(text: String): String = {
    val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
    val skeySpec = new SecretKeySpec(hashToFixedLength(key).getBytes("UTF-8"), "AES")

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

    val encrypted = cipher.doFinal(text.getBytes())
    return Base64.getEncoder().encodeToString(encrypted)
  }

  def decrypt(text: String): String = {
    val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
    val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
    val original = cipher.doFinal(Base64.getDecoder.decode(text))
    new String(original)
  }
}


object TestEncrypt {
  private val encrypt = Encrypt("enIntVecTest2020");

  def main (args: Array[String]) = {
    println(encrypt.encrypt("Hello, world"))
  }
}

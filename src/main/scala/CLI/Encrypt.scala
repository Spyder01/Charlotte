package CLI

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import javax.crypto.BadPaddingException
import scala.util.Random

case class Encrypt(key: String) {
  private val initVector = Random.alphanumeric.take(16).mkString;

  private def createHash(input: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(input.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString
  }
  private def hashToFixedLength (text: String): String = {
    val doubleHash = createHash(createHash(createHash(text)))
    doubleHash.reverse.take(8) + doubleHash.take(8)
  }
  def encrypt(text: String): String = {
    val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
    val skeySpec = new SecretKeySpec(hashToFixedLength(key).getBytes("UTF-8"), "AES")

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

    val encrypted = cipher.doFinal(text.getBytes())
    return initVector + Base64.getEncoder().encodeToString(encrypted)
  }

  def decrypt(encrypted_text: String): String = {
    try {
      val initVector = encrypted_text.slice(0, 16)
      val text = encrypted_text.slice(16, encrypted_text.length);
      val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
      val skeySpec = new SecretKeySpec(hashToFixedLength(key).getBytes("UTF-8"), "AES")

      val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
      val original = cipher.doFinal(Base64.getDecoder.decode(text))
      new String(original)
    } catch {
      case e: BadPaddingException => throw new Exception("Invalid Key");
      case _ => throw new Exception("Sorry! Unexpected error occured while decrypting.")
    }
  }
}

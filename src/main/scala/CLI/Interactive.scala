package CLI

import scala.annotation.tailrec
import scala.io.StdIn.{readChar, readLine}


object Interactive {

  case class Data (encrypt: Boolean, path: String, destination: String, password: String);

  def ask(): Data = {
    val encrypt = askEncrypted()
    val path = askLocation ("Enter the location of folder or file to be encrypted/decrypted: ");
    val storePath = askLocation("Enter the location where you want to store your encrypted/decrypted file/files: ")
    val password = askPassword();
    Data(encrypt, path, storePath, password);
  }

  private def askEncrypted (): Boolean = {
    print("What do you want to do? Encryption(E) or Decryption(D): ")
    val ch: Char = readChar()
    ch.toLower match {
      case 'e' => true
      case 'd' => false
      case _ => throw new Exception("Invalid choice")
    }
  }
@tailrec
  private def askLocation (msg: String): String = {
    var path = ""
      print(msg)
      path = readLine();
     if (FileHandling.validatePath(path, ()=>print("Invalid Location. "))) path else askLocation(msg)
  }

  @tailrec
  private def askPassword(): String = {
    var password: String = ""
      print("Enter your password: ");
      password = readLine()
    if (validatePassword(password, ()=>print("Password should be at least 8 characters long: "))) password else askPassword();
  }

  private def validatePassword (password: String, callback:()=>Unit = ()=>{} ): Boolean = {
    if (password.length > 8) true else {
      callback()
      false
    }
  }
}

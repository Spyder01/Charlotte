package CLI

import scala.io.StdIn.{readChar, readLine}


object Interactive {

  case class Data (encrypt: Boolean, path: String, destination: String, password: String);

  def ask(): Data = {
    val encrypt = askEncrypted()
    val path = askLocation ("Enter the location of folder or file to be encrypted/decrypted: ");
    val storePath = askLocation("Enter the location where you want to store your encrypted/decrypted file/files.")
    val password = askPassword();
    new Data(encrypt, path, storePath, password);
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

  private def askLocation (msg: String): String = {
    var path = ""

   do  {
      print(msg)
      path = readLine();
    } while (!FileHandling.validatePath(path, ()=>print("Invalid Location. ")));

    return path
  }

  private def askPassword (): String = {
    var password: String
    do {
      print("Enter your password: ");
      password = readLine()
    } while (!validatePassword(password, ()=>print("Password should be at least 8 characters long: ")))
    password
  }

  private def validatePassword (password: String, callback:()=>Unit = ()=>{} ): Boolean = {
    if (password.length > 8) {
      return true;
    }
    callback()
    return false
  }
}

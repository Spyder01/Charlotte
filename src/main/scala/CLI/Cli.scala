package CLI

import CLI.FileHandling.{createManipulatedDirectory, createManipulatedFile, isFile, isFolder}
import CLI.Interactive.Data

object Cli {

  private var encrypt: Encrypt = null;
  def start ():Unit = {
    val data = Interactive.ask();
    encrypt = new Encrypt(data.password);
    data match {
      case _ if data.encrypt => println("Encryption Done")
      case _ if  !data.encrypt => println("Decryption Done")
      case _ => new Exception("Invalid Mode. Mode should be either encrypt or decrypt")
    }
  }
  private def operate (data: Data, operation: (String)=>String): Unit = {
    val tag = if (data.encrypt) "encrypted" else "decrypted"
    if (isFolder(data.path)) {
      createManipulatedDirectory(data.path, data.destination, operation, tag)
    }
    if (isFile(data.path)) {
      createManipulatedFile(data.path, data.destination, operation, tag)
    }
  }
}

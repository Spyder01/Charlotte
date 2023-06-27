package CLI

import scala.io.StdIn.readLine


object Interactive {

  case class Data (path: String, password: String);

  def ask(): Data = {
    val path = askLocation ();
    val password = askPassword();
    return new Data(path, password);
  }

  private def askLocation (): String = {
    var path = ""

   do  {
      print("Enter the location of folder or file to be encrypted: ")
      path = readLine();
    } while (!FileHandling.validatePath(path, ()=>print("Invalid Location. ")));

    return path
  }

  private def askPassword (): String = {
    var password = ""
    do {
      print("Enter your password: ");
      password = readLine()
    } while (!validatePassword(password, ()=>print("Password should be at least 8 characters long: ")))
    return  password
  }

  private def validatePassword (password: String, callback:()=>Unit = ()=>{} ): Boolean = {
    if (password.length > 8) {
      return true;
    }
    callback()
    return false
  }
}

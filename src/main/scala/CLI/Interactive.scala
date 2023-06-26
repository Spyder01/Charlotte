package CLI

import scala.io.StdIn.readLine


object Interactive {

  def ask(): String = {
    val path = askLocation ();
    return path;
  }

  private def askLocation (): String = {
    var path = ""

   do  {
      print("Enter the location of folder or file to be encrypted: ")
      path = readLine();
    } while (!FileHandling.validatePath(path, ()=>print("Invalid Location. ")));

    return path
  }
}

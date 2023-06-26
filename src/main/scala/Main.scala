import CLI.{Cli, Interactive}

object Main {
  val help =
    """
      Welcome to File Encryptor!!
      |""".stripMargin
  def main (args: Array[String]): Unit = {
    args match {
      case _ if args.length == 0 => Cli.start();
      case _ => println("Invalid arguments");
    }
  }
}

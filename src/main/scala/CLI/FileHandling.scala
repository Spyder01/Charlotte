package CLI

import java.nio.channels.{FileChannel, FileLock}
import java.nio.file.{FileSystem, FileSystems, Files, NoSuchFileException, Path, Paths, StandardOpenOption}
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.jdk.CollectionConverters._
import java.util.concurrent.locks.ReentrantLock
import scala.io.Source

object FileHandling {
  private val fileMap: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map();
  private val lock = new ReentrantLock()

  def validatePath(path: String, callback: () => Unit): Boolean = {
    if (Files.exists(Paths.get(path))) {
      return true;
    }
    callback()
    return false;
  }

  def isFile(path: String): Boolean = Files.isRegularFile(Paths.get(path))
  def isFolder(path: String): Boolean = Files.isDirectory(Paths.get(path))

  def createManipulatedDirectory(path: String, newPath: String, manipulater: (String)=>String) = {
      val directoryName = getBaseName(path)
      val rootDirectoryPath = joinPaths(newPath, directoryName)
      createDirectory(rootDirectoryPath)
      val paths = getAllFilePaths(Paths.get(path))

      paths.foreach{(_path)=>{
        val newPath = joinPaths(rootDirectoryPath, removeRootPath(_path.toString, path))
        val bufferedSource = Source.fromFile(_path.toString)
        val fileContents = bufferedSource.getLines.mkString("\n")
        println(manipulater(fileContents))
        createFile(newPath, manipulater(fileContents))
        bufferedSource.close()
      }

      }
  }

  private def removeRootPath(path: String, rootPath: String): String = {
    Paths.get(rootPath).relativize(Paths.get(path)).toString
  }

  private def createDirectory(path: String, callback: ()=>Unit = ()=>{}): Unit = {
//    lock.lock()
    try {
      if (!fileMap.contains(path) && !Files.exists(Paths.get(path))) {
        Files.createDirectory(Paths.get(path));
        fileMap(path) = true
      }
    }
    catch {
      case e: NoSuchFileException => createDirectory(getParent(path), ()=>createDirectory(path));
    } finally {
      callback()
//      lock.unlock()
    }
  }

  private def createFile(path: String, contents: String, callback: () => Unit = () => {}): Unit = {
    try {
      if (!fileMap.contains(path)) {
        Files.writeString(Paths.get(path), contents)
        fileMap(path) = true
      }
    } catch {
      case e: NoSuchFileException => createDirectory(getParent(path), () => createFile(path, contents))
      case e: Exception => println(s"Error writing file: ${e.getMessage}")
    } finally {
      callback()
    }
  }

  private def getAllFilePaths(directoryPath: Path): Seq[Path] = {
    val directoryStream = Files.newDirectoryStream(directoryPath)
    try {
      directoryStream.asScala.toSeq;
    } finally {
      directoryStream.close()
    }
  }

  private def getBaseName(filepath: String): String = Paths.get(filepath).getFileName.toString

  private def getParent(filePath: String): String = {
    val path = Paths.get(filePath)
    val fileSystem: FileSystem = FileSystems.getDefault
    val root = path.getRoot
    val rootString = if (root != null) root.toString else ""
    val separator = fileSystem.getSeparator

    val parentPath = path.iterator().asScala.map(_.toString).toArray.init
    (rootString +: parentPath).mkString(separator)
  }

  private def joinPaths(segments: String*): String = {
    val path: Path = Paths.get(segments.headOption.getOrElse(""), segments.tail: _*)
    path.toString
  }

  def main (args: Array[String]): Unit = {
    createManipulatedDirectory("C:\\Users\\SJB\\cast_projects\\fastscan\\test_folders\\testtt", "C:\\Users\\SJB\\cast_projects\\fastscan\\test_folders\\testt22323t2", (str: String)=>"dhhdhd")
  }
}

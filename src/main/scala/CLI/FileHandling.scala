package CLI

import java.nio.channels.{FileChannel, FileLock}
import java.nio.file.{FileSystem, FileSystems, Files, NoSuchFileException, Path, Paths, StandardOpenOption}
import scala.jdk.CollectionConverters.IteratorHasAsScala
import java.util.concurrent.locks.ReentrantLock

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
  }

  private def createDirectory(path: String, callback: ()=>Unit = ()=>{}): Unit = {
    lock.lock()
    try {
      if (!fileMap.contains(path)) {
        Files.createDirectory(Paths.get(path));
        fileMap(path) = true
      }
    }
    catch {
      case e: NoSuchFileException => createDirectory(getParent(path), ()=>createDirectory(path));
    } finally {
      callback()
      lock.unlock()
    }
  }

  private def createFile(path: String, contents: String, callback: ()=>Unit = ()=>{}): Unit = {
    lock.lock()
    try {
      if (!fileMap.contains(path)) {
        val filePath = Paths.get(path)
        val fileChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        val fileLock: FileLock = fileChannel.lock()
        try {
          Files.write(filePath, contents.getBytes("UTF-8"))
        } finally {
          fileLock.release()
          fileChannel.close()
        }
        fileMap(path) = true
      }
    }
    catch {
      case e: NoSuchFileException => createDirectory(getParent(path), () => createFile(path, contents));
    } finally {
      callback()
      lock.unlock()
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
    val path: Path = Paths.get(segments.head, segments.tail: _*)
    path.toString
  }


  def main (args: Array[String]): Unit = {
    createDirectory("C:\\Users\\SJB\\cast_projects\\test\\test\\test")
  }
}

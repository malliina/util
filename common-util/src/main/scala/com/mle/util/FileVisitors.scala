package com.mle.util

import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

/**
 * Directory listing classes using Java 7 APIs.
 * @author Mle
 */
object FileVisitors {
  def build(srcDir: String, recursive: Boolean = true, ageLimitHours: Option[Long] = None, sortByAge: Boolean = true) = {
    if (recursive) {
      if (sortByAge) {
        ageLimitHours.map(age =>
          new AgeLimitFilteringFileVisitor(age) with FileSorting
        ).getOrElse(
          new FilteringFileVisitor with FileSorting
        )
      } else {
        ageLimitHours.map(age =>
          new AgeLimitFilteringFileVisitor(age)
        ).getOrElse(
          new FilteringFileVisitor
        )
      }

    } else {
      if (sortByAge) {
        ageLimitHours.map(age =>
          new AgeLimitFilteringFileVisitor(age) with FileSorting with NonRecursiveSearch {
            val startDir = srcDir
          }
        ).getOrElse(
          new FilteringFileVisitor with FileSorting with NonRecursiveSearch {
            val startDir = srcDir
          }
        )
      } else {
        ageLimitHours.map(age =>
          new AgeLimitFilteringFileVisitor(age) with NonRecursiveSearch {
            val startDir = srcDir
          }
        ).getOrElse(
          new FilteringFileVisitor with NonRecursiveSearch {
            val startDir = srcDir
          }
        )
      }

    }
  }

  /**
   * Visitor for directory traversals with a file buffer.
   */
  abstract class FileCollectingVisitor extends SimpleFileVisitor[Path] {
    private[this] var fileBuffer: List[Path] = Nil

    /**
     * @return the files that have been visited and added to the buffer
     */
    def files = fileBuffer

    def add(path: Path) {
      fileBuffer = path :: fileBuffer
    }
  }

  /**
   * File visitor that adds visited regular files to a buffer;
   * comes with an optional filter that subclasses can implement;
   * by default all files are included.
   */
  class FilteringFileVisitor extends FileCollectingVisitor with Log {
    override def visitFile(file: Path, attrs: BasicFileAttributes) = {
      if (attrs.isRegularFile && qualifies(file, attrs)) {
        add(file)
      }
      FileVisitResult.CONTINUE
    }

    /**
     * Override to filter files as you wish. Qualified files can be queried using the <code>files</code> member.
     * @param file the file
     * @param attrs the file's attributes
     * @return true if the file qualifies, false otherwise
     */
    def qualifies(file: Path, attrs: BasicFileAttributes) = true
  }

  /**
   * Filtering file visitor for old files.
   * @param ageLimitHours the threshold age in hours: files with an older timestamp, as defined by <code>comparisonTimestamp</code>, will qualify for inclusion
   */
  class AgeLimitFilteringFileVisitor(ageLimitHours: Long) extends FilteringFileVisitor with TimestampComparison with Log {
    val ageLimitMillis = ageLimitHours * 60 * 60 * 1000

    override def qualifies(file: Path, attrs: BasicFileAttributes) = {
      val currentTime = System.currentTimeMillis()
      val comparisonTime = comparisonTimestamp(attrs)
      val fileAge = currentTime - comparisonTime
      //			log info "The age of " + file.getFileName + " was " + (1.0 * fileAge / 1000 / 60 / 60 / 24) + " days, comparing to: " + 1.0 * ageLimitHours / 24 + " days: " + (fileAge > ageLimitMillis)
      fileAge > ageLimitMillis
    }
  }

  /**
   * Visitor for directory traversals. Visited files and directories are available in the <code>paths</code> member.
   */
  class FileAndDirCollector extends FileCollectingVisitor {

    override def visitFile(file: Path, attrs: BasicFileAttributes) = handlePath(file)

    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = handlePath(dir)

    private def handlePath(path: Path) = {
      add(path)
      FileVisitResult.CONTINUE
    }
  }

  //	class RegexVisitor(pattern: Pattern) extends FilteringFileVisitor with Log {
  //		def this(regex: String) = this(Pattern compile regex)
  //
  //		override def qualifies(file: Path, attrs: BasicFileAttributes) = RegexUtils.matches(pattern, file.getFileName.toString)
  //	}

  trait TimestampComparison {
    /**
     * A file timestamp used for comparisons in the file filter.
     * @param attrs file attrs
     * @return a timestamp in milliseconds since the epoch, usually the create or last modified timestamp
     */
    def comparisonTimestamp(attrs: BasicFileAttributes) = math.max(attrs.lastModifiedTime().toMillis, attrs.creationTime().toMillis)
  }

  trait FileSorting extends FileCollectingVisitor with TimestampComparison {
    override def files = super.files.sortBy(file => comparisonTimestamp(Files.readAttributes(file, classOf[BasicFileAttributes])))
  }

  trait NonRecursiveSearch extends SimpleFileVisitor[Path] with Log {
    def startDir: String

    lazy val startPath = Paths get startDir

    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = {
      if (Files.isSameFile(startPath, dir))
        FileVisitResult.CONTINUE
      else
        FileVisitResult.SKIP_SUBTREE
    }
  }

}

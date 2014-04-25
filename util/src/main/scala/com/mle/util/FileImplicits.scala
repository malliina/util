package com.mle.util

import java.nio.file.{Files, Path}
import com.mle.storage.{StorageSize, StorageLong}

/**
 *
 * @author mle
 */
object FileImplicits {

  implicit final class StorageFile(val file: Path) {
    def size: StorageSize = (Files size file).bytes

    def /(next: String): Path = file resolve next

    def /(next: Path): Path = file resolve next
  }

}

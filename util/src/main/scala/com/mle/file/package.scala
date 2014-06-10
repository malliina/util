package com.mle

import java.nio.file.{Files, Path}
import com.mle.storage.StorageSize
import com.mle.storage.StorageLong

/**
 * @author Michael
 */
package object file {

  implicit final class StorageFile(val file: Path) {
    def size: StorageSize = (Files size file).bytes

    def /(next: String): Path = file resolve next

    def /(next: Path): Path = file resolve next
  }

}

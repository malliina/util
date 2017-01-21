package com.malliina.azure

import java.io.{FileInputStream, FileOutputStream}
import java.net.URI
import java.nio.file.{Files, Path}

import com.malliina.util.Util
import com.microsoft.azure.storage.blob.{CloudBlobContainer, CloudBlockBlob}

import scala.collection.JavaConversions._
import scala.util.Try

class StorageContainer(val cont: CloudBlobContainer) {
  val name = cont.getName

  def uris = cont.listBlobs().map(_.getUri)

  def existsUri(uriString: String) = uris.exists(_.toString == uriString)

  def exists(blobName: String) = withBlob(blobName) { blob => Try(blob.exists()) }

  /** Uploads the file to this azure storage container.
    *
    * @param file     file to upload
    * @param destName remote file name
    * @return URI to the uploaded file
    */
  def upload(file: Path, destName: String): Try[URI] = {
    withBlob(destName) { blob =>
      Try {
        Util.using(new FileInputStream(file.toFile)) { inStream =>
          blob.upload(inStream, Files size file)
        }
        blob.getUri
      }
    }
  }

  def upload(file: Path): Try[URI] =
    upload(file, file.getFileName.toString)

  def download(blobName: String, destination: Path) =
    withBlob(blobName) { blob =>
      Try {
        Util.using(new FileOutputStream(destination.toFile)) { stream =>
          blob.download(stream)
        }
      }
    }

  def delete(blobName: String) = withBlob(blobName)(blob => Try(blob.delete()))

  def withBlob[T](blobName: String)(f: CloudBlockBlob => Try[T]) =
    blobNamed(blobName).flatMap(f)

  def blobNamed(blobName: String): Try[CloudBlockBlob] =
    Try(cont getBlockBlobReference blobName)
}

package com.malliina.azure

import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer
import java.nio.file.{Files, Path}
import com.malliina.util.Util
import java.io.{FileOutputStream, FileInputStream}
import scala.collection.JavaConversions._
import java.net.URI

/**
 *
 * @author mle
 */
class StorageContainer(val cont: CloudBlobContainer) {
  val name = cont.getName

  def uris = cont.listBlobs().map(_.getUri)

  def existsUri(uriString: String) = uris.exists(_.toString == uriString)

  def exists(blobName: String) =
    blobNamed(blobName).exists()

  /**
   * Uploads the file to this azure storage container.
   *
   * @param file file to upload
   * @param destName remote file name
   * @return URI to the uploaded file
   */
  def upload(file: Path, destName: String): URI = {
    val blob = blobNamed(destName)
    Util.using(new FileInputStream(file.toFile))(inStream => {
      blob.upload(inStream, Files size file)
    })
    blob.getUri
  }

  def upload(file: Path): URI =
    upload(file, file.getFileName.toString)

  def download(blobName: String, destination: Path) {
    val blob = blobNamed(blobName)
    Util.using(new FileOutputStream(destination.toFile))(stream => {
      blob.download(stream)
    })
  }

  def delete(blobName: String) {
    blobNamed(blobName).delete()
  }

  def blobNamed(blobName: String) =
    cont getBlockBlobReference blobName
}
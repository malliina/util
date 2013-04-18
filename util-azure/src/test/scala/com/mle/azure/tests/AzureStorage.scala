package com.mle.azure.tests

import org.scalatest.FunSuite
import com.mle.util.Implicits._
import java.nio.file.{Files, Paths}
import com.mle.util.{Util => Utils, FileUtilities}
import com.mle.azure.StorageClient
import com.microsoft.windowsazure.services.blob.client.{BlobRequestOptions, BlobListingDetails}
import java.util.EnumSet
import com.microsoft.windowsazure.services.core.storage.OperationContext
import scala.collection.JavaConversions._

/**
 * Needs a credentials file in userHome/keys/azure-storage.sec
 * with keys account_name and account key, and a container in Azure named "files".
 *
 * @author mle
 */
class AzureStorage extends FunSuite {
  val userHome = Paths get sys.props("user.home")
  val credentialsFile = userHome / "keys" / "azure-storage.sec"
  val credMap = Utils.props(credentialsFile.toAbsolutePath.toString)
  val accountName = credMap("account_name")
  val accountKey = credMap("account_key")
  val containerName = "files"

  private def newClient =
    new StorageClient(accountName, accountKey)

  test("can read from storage") {
    val uris = newClient uris "files"
    //    uris foreach println
  }
  test("can upload, download and delete file") {
    val testFileName = "azuretest.txt"
    val testDownloadFile = "dl-" + testFileName
    val downloadDest = Paths get testDownloadFile
    val testContent = "Hello, there!"
    val testFile = FileUtilities.writerTo(testFileName)(_.println(testContent))
    val client = newClient
    val cont = client container containerName
    val uri = cont upload testFile
    assert(cont.exists(testFileName))
    //    println("Test file uploaded to " + uri)
    cont.download(testFileName, downloadDest)
    val firstLine = FileUtilities.readerFrom(downloadDest)(_.next())
    assert(firstLine === testContent)
    Files.deleteIfExists(downloadDest)
    cont delete testFileName
    assert(!cont.exists(testFileName))
    Files delete testFile
  }
  test("read logs") {
    val client = newClient
    val conts = client.containers
    //    conts.map(_.name) foreach println
    val logCont = client container "$logs"

    assert(logCont.cont.exists())
    val blobs = logCont.cont.listBlobs("blob", true, EnumSet.noneOf(classOf[BlobListingDetails]), new BlobRequestOptions, new OperationContext)
    blobs.map(_.getUri) foreach println
  }
}

package tests

import org.scalatest.FunSuite
import java.nio.file.{Files, Paths}
import com.mle.util.FileUtilities
import com.microsoft.windowsazure.services.blob.client.{BlobRequestOptions, BlobListingDetails}
import java.util.EnumSet
import com.microsoft.windowsazure.services.core.storage.{MetricsLevel, OperationContext}
import collection.JavaConversions._
import com.microsoft.windowsazure.services.table.client.{TableConstants, TableServiceEntity, TableQuery}
import com.microsoft.windowsazure.services.table.client.TableQuery.QueryComparisons

/**
 * Needs a credentials file in userHome/keys/azure-storage.sec
 * with keys account_name and account key, and a container in Azure named "files".
 *
 * @author mle
 */
class AzureStorage extends FunSuite with TestBase {
  test("can read from storage") {
    val uris = newClient uris "files"
    uris foreach println
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
    //    println("Test file: " + testFileName + " uploaded to: " + uri)
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
    //    logCont.download()
    //      blobs.map(_.getUri) foreach println
    // http://pimp.blob.core.windows.net/$logs/blob/2013/05/22/1400/000000.log
  }
  test("log exists") {
    val logFile = "blob/2013/05/22/1400/000000.log"
    val client = newClient
    val logCont = client container "$logs"
    assert(logCont.cont.exists())
    assert(logCont exists logFile)
  }
  test("download log") {
    val logFile = "blob/2013/05/22/1400/000000.log"
    val client = newClient
    val logCont = client container "$logs"
    val dest = Paths get "dl-log.log"
    logCont.download(logFile, dest)
    val file = FileUtilities.readerFrom(dest)(_.toList)
    file foreach println
  }

  test("list tables") {
    val client = newClient
    println(s"Tables: ${client.tables.size}")
    assert(client.blobClient.downloadServiceProperties().getMetrics.getMetricsLevel === MetricsLevel.SERVICE)
    client.tables foreach println
    val tableClient = client.tableClient
    val tableName = "$MetricsHourPrimaryTransactionsBlob"
    val table = tableClient.getTableReference(tableName)
    assert(table.exists(), "Table must exist")
    val query = TableQuery.from(tableName, classOf[TestEntity])
    val filter1 = TableQuery.generateFilterCondition(TableConstants.PARTITION_KEY, QueryComparisons.GREATER_THAN, "20140308T0800")
    val filter2 = TableQuery.generateFilterCondition(TableConstants.PARTITION_KEY, QueryComparisons.LESS_THAN, "20140408T0800")
    val filteredQuery = query.where(filter1).where(filter2)
    val entities = tableClient.execute(filteredQuery)
//    println(s"Got ${entities.size} entities")
    entities.foreach(entity => println(entity.getTotalRequests))
  }


}

class TestEntity extends TableServiceEntity {
  private var totalRequests: Long = 0

  def getTotalRequests: Long = totalRequests

  def setTotalRequests(requests: Long): Unit = totalRequests = requests
}
package com.malliina.azure

import com.microsoft.windowsazure.services.blob.client.{BlobRequestOptions, BlobListingDetails, CloudBlobContainer}
import java.util.EnumSet
import com.microsoft.windowsazure.services.core.storage.OperationContext
import scala.collection.JavaConversions._

/**
 *
 * @author mle
 */
class LogStorageContainer(cont: CloudBlobContainer)
  extends StorageContainer(cont) {
  def logUris = cont.listBlobs("blob", true, EnumSet.noneOf(classOf[BlobListingDetails]), new BlobRequestOptions, new OperationContext).map(_.getUri)

}

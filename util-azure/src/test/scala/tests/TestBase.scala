package tests

import java.nio.file.Paths
import com.mle.util.Util
import com.mle.azure.StorageClient
import com.mle.util.Implicits._

/**
 *
 * @author mle
 */
trait TestBase {
  val userHome = Paths get sys.props("user.home")
  val credentialsFile = userHome / "keys" / "azure-storage.sec"
  val credMap = Util.props(credentialsFile.toAbsolutePath.toString)
  val accountName = credMap("account_name")
  val accountKey = credMap("account_key")
  val containerName = "files"

  protected def newClient =
    new StorageClient(accountName, accountKey)
}

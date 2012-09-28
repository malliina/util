import java.nio.file.Path

/**
 * @author Mle
 */
object Implicits {
  implicit def p2f(path: Path) = path.toFile

  implicit def path2path(path: Path) = new {
    def /(next: String) = path resolve next

    def /(next: Path) = path resolve next
  }
}

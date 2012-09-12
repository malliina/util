package models

import com.mle.util.Log

/**
 *
 * @author Mle
 */
case class Task(id: Long, label: String)

object Task extends Log {
  private var tasks: List[Task] = Nil
  private var id = 0

  def all(): List[Task] = tasks

  def create(label: String) {
    id += 1
    tasks = Task(id, label) :: tasks
    log info "Created task"
  }

  def delete(id: Long) {
    tasks = all().filterNot(_.id == id)
    log info "Deleted task with id: " + id
  }
}
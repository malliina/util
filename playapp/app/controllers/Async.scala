package controllers

import play.api.mvc.{Action, Controller}
import util.PlayLog
import com.mle.util.Log
import models.jdbc.PlayDb.PlaySchema._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object Async extends Controller with Log {
  val playLog = new PlayLog {

  }

  def index = Action {
    log info "TODO: this log statement is only printed if the app is started with " +
      "play$ start -Dlogger.resource=logback.xml " +
      "because play fucks up the logback configuration"
    playLog.log info "This works: loading!"
    val res = Akka.future({
      playLog.log info "This will take long ..."
      Thread sleep 4000
      playLog.log info "Done with long task"
      testtable.select(testtable.a.name)()(_ getInt 1)
    })
    playLog.log info "Server promises result and moves on..."
    Async(res.map(r => Ok(r mkString (", "))))
  }
}

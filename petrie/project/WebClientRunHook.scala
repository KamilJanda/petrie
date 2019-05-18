import play.sbt.PlayRunHook
import sbt._
import scala.sys.process.Process


object WebClientRunHook {

  val webClientDirName = "web-client"

  def apply(base: File): PlayRunHook = {
    object WebClientBuildHook extends PlayRunHook {

      import FrontendCommands._

      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        if (!(base / webClientDirName / "node_modules").exists())
          Process(dependencyInstall, base / webClientDirName).!
      }

      override def afterStarted(): Unit = {
        process = Option(
          Process(run, base / webClientDirName).run
        )
      }

      override def afterStopped(): Unit = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebClientBuildHook
  }
}
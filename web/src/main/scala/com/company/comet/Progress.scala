package com.company.comet

import net.liftweb.http.{CometListener, CometActor}
import org.slf4j.LoggerFactory

/**
 * // TODO description
 * @author Oleg Aravin
 */
class Progress extends CometActor with CometListener {

  val logger = LoggerFactory.getLogger(classOf[Progress])

  private var event: UpdateStatus = UpdateStatus("")

  protected def registerWith = NetworksServer

  override def lowPriority = {

    case event: UpdateStatus => {
      this.event = event
      logger.info("Updating the status to: " + event.status)
      reRender()
    }

    case _ =>

  }

  // display the items
  def render = "#status *" #> event.status
}

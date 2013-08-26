package com.company.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager

/**
 * @author Oleg Aravin
 */
object NetworksServer extends LiftActor with ListenerManager {

  private val msgs: Event = Init()

  def createUpdate = msgs

  override def lowPriority = {
    case _ => // todo
  }
}

package com.company.comet

import net.liftweb.http.{S, CometActor, CometListener}
import net.liftweb.util.CssSel
import org.slf4j.LoggerFactory
import com.erkfel.model.{NetworkViewRef, NetworkView, IP}
import com.erkfel.model.SearchMode._
import com.erkfel.vcloud.VCloudService
import scala.concurrent.ops._
import com.company.comet.Networks._
import net.liftweb.common.Empty


/**
 * @author Oleg Aravin
 */

class Networks extends CometActor with CometListener {

  val logger = LoggerFactory.getLogger(classOf[Networks])

  private var networks = Seq[NetworkView]()
  private var networksRefs = Seq[NetworkViewRef]()
  private var checkNetworkRange = true
  private var searchMode = All
  private var ipStart: IP = _
  private var ipEnd: IP = _

  /**
   * When the component is instantiated, register as
   * a listener with the NetworksServer
   */
  def registerWith = NetworksServer

  /**
   * The CometActor is an Actor, so it processes messages.
   * In this case, we're listening for Vector[String],
   * and when we get one, update our private state
   * and reRender() the component.  reRender() will
   * cause changes to be sent to the browser.
   */
  override def lowPriority = {

    case event: Search => {
      logger.info("Receive event " + event + " in actor " + this)
      updateState(event)
      val adapter = VCloudService(event.vCloud)

      logger.info("Getting the initial networks for the actor " + this)
      if (!event.useCache) {
        networksRefs = adapter.getNetworksRefsView
        networks = Seq()
      }
      reRender()

      spawn {
        try {
          logger.info("Getting the detailed networks view for the actor " + this)
          if (!event.useCache) networks = adapter.getNetworksDetailedView
          logger.info("Received " + networks.size + " detailed networks view for the actor " + this)
        } catch {
          case e => updateStatus("Error " + e.getClass.toString)
        } finally {
          reRender()
        }
      }
    }
  }

  def updateStatus(status: String) {
    for {
      sess <- S.session
    } {
      sess.sendCometActorMessage("Progress", Empty, UpdateStatus(status))
    }
    reRender()
  }

  def updateState(event: Search) {
    searchMode = event.mode
    checkNetworkRange = event.checkNetworkRange
    ipStart = event.ipStart
    ipEnd = event.ipEnd
  }

  def render = {
    logger.info("Start rendering actor " + this)
    "tbody *" #> renderItems(networks, networksRefs, searchMode, ipStart, ipEnd)
  }

  def renderItems(networkViews: Seq[NetworkView], networkViewsRef: Seq[NetworkViewRef], searchMode: SearchMode, ipStart: IP, ipEnd: IP) = {
    logger.info("Networks list size is {} in actor {}", networkViews.size, this)

    networks.isEmpty match {
      case true => "tr" #> networksRefs.map(create)
      case false => "tr" #> increasingSort(filter(networkViews, searchMode, ipStart, ipEnd, checkNetworkRange)).map(create)
    }
  }

  def create(network: NetworkView): CssSel = {
    "tr [id]" #> network.name &
      "@network *" #> {
        "li *" #> {
          network.ipRange match {
            case Some(ipRange) => Seq(network.name, ipRange.start.toString, ipRange.end.toString)
            case _ => Seq(network.name)
          }
        }
      } &
      "@vapps *" #> {
        "li *" #> network.vApps.map(vAppView => vAppView.name)
      } &
      "@ips *" #> {
        "li *" #> network.vApps.map(
          vAppView => getNetworkIPsView(vAppView.vAppIPsPerNetwork.getOrElse(network.name, List[IP]()).sortWith(_ < _))
        )
      } &
      "@vapps_count *" #> network.vApps.size.toString
  }

  def create(networkRef: NetworkViewRef): CssSel = {
    "tr [id]" #> networkRef.name &
      "@network *" #> {
        "li *" #> networkRef.name
      } &
      "@vapps *" #> {
        "li *" #> pending
      } &
      "@ips *" #> {
        "li *" #> pending
      } &
      "@vapps_count *" #> pending
  }
}

object Networks {

  val pending = "pending..."

  def getNetworkIPsView(ips: List[IP]): String = {
    ips.size match {
      case 0 => "No IPs"
      case 1 => ips.head.toString
      case _ => ips.head.toString + "..." + ips.last.toString + " (" + ips.size + " in use)"
    }
  }

  def filter(networks: Seq[NetworkView], mode: SearchMode, ipStart: IP, ipEnd: IP, checkNetworkRange: Boolean): Seq[NetworkView] = {
    networks.filter(
      networkView => {
        val isInNetwork = (networkView.ipRange, checkNetworkRange) match {
          case (Some(ipRange), true) => isInRange(ipStart, ipRange.start, ipRange.end) && isInRange(ipEnd, ipRange.start, ipRange.end)
          case _ => true
        }
        mode match {
          case All => true
          case Free => isInNetwork && !networkView.usedIPs.exists(usedIP => isInRange(usedIP, ipStart, ipEnd))
          case Busy => isInNetwork && networkView.usedIPs.exists(usedIP => isInRange(usedIP, ipStart, ipEnd))
        }
      }
    )
  }

  def isInRange(ip: IP, ipStart: IP, ipEnd: IP): Boolean = (ip > ipStart || ip == ipStart) && (ip < ipEnd || ip == ipEnd)

  def increasingSort(networkViews: Seq[NetworkView]): Seq[NetworkView] = networkViews.sortWith((v1, v2) => v1.vApps.size > v2.vApps.size)
}

sealed class Event()

case class Init() extends Event

case class UpdateStatus(status: String) extends Event

case class Search(vCloud: String, ipStart: IP, ipEnd: IP, mode: SearchMode, useCache: Boolean = false, checkNetworkRange: Boolean = true) extends Event

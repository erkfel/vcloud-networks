package com.company.snippet

import net.liftweb._
import http._
import util.Helpers._
import org.slf4j.LoggerFactory
import net.liftweb.common.{Empty, Full}
import com.company.comet.Search
import com.erkfel.model.{SearchMode, IP}
import com.erkfel.properties.VCloudPropertiesLoader

class FilterNetwork extends StatefulSnippet {

  val logger = LoggerFactory.getLogger(classOf[FilterNetwork])

  private var ipStart = "192.168.211.1"
  private var ipEnd = "192.168.211.255"
  private val vCloudList = VCloudPropertiesLoader.properties.map(_.orgName)
  private val searchModeList = List("All", "Free", "Busy")
  private var vCloud = vCloudList.head
  private var searchMode = searchModeList.head
  private var useCache = false
  private var checkNetworkRange = false

  def dispatch = {
    case "render" => {
      logger.info("Render")
      render
    }
  }

  def render =
    "name=ip_from" #> SHtml.text(ipStart, ipStart = _) &
      "name=ip_to" #> SHtml.text(ipEnd, ipEnd = _) &
      "name=vCloudSelect" #> SHtml.select(vCloudList.map(v => (v, v)), Full(vCloud), res => vCloud = res) &
      "name=searchModeSelect" #> SHtml.select(searchModeList.map(v => (v, v)), Full(searchMode), res => searchMode = res) &
      "name=checkBoxCache" #> SHtml.checkbox(useCache, useCache = _) &
      "name=checkBoxNetworkRange" #> SHtml.checkbox(checkNetworkRange, checkNetworkRange = _) &
      "type=submit" #> SHtml.onSubmitUnit(process)

  private def process() = {
    logger.info("Ip from {} ", ipStart)
    logger.info("Ip to {} ", ipEnd)
    logger.info("vCloud {} ", vCloud)
    logger.info("Search mode {} ", searchMode)
    logger.info("Use cache ", useCache)
    logger.info("Check network range", checkNetworkRange)

    for {
      sess <- S.session
    } {
      sess.sendCometActorMessage("Networks", Empty, Search(vCloud, IP(ipStart), IP(ipEnd), SearchMode.withName(searchMode), useCache, checkNetworkRange))
    }
  }


}


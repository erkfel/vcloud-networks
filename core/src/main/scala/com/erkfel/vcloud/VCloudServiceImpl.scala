package com.erkfel.vcloud

import com.erkfel.model._
import org.slf4j.LoggerFactory
import com.erkfel.model.NetworkView
import com.erkfel.vcloud.VCloudClientAdapter._
import scala.actors.Actor

/**
 * @author Oleg Aravin
 */

class VCloudServiceImpl(vCloudAdapter: VCloudClientAdapter) extends VCloudService {

  val logger = LoggerFactory.getLogger(classOf[VCloudServiceImpl])


  def getNetworksRefsView: Seq[NetworkViewRef] = {
    logger.info("Request to fetch network references of organization " + vCloudAdapter.orgName)
    val refs = vCloudAdapter.networkRefs.map(ref => NetworkViewRef(ref.getName))
    logger.info("Found " + refs.size + " network references of organization " + vCloudAdapter.orgName)
    logger.debug("Network references of organization " + vCloudAdapter.orgName + " are " + refs.toString())
    refs
  }

  def getNetworkDetailedView(networkViewRef: NetworkViewRef): NetworkView = {
    val start = System.currentTimeMillis()
    logger.info("Request to fetch network " + networkViewRef + " details from organization " + vCloudAdapter.orgName)
    val vApps = vCloudAdapter.vappRefs.map(vAppRef => vCloudAdapter.vappByName(vAppRef.getName))
    val result = toView(vCloudAdapter.networkByName(networkViewRef.name), vApps)
    logger.info("Request to fetch network " + networkViewRef + " details time: " + (System.currentTimeMillis() - start) + " ms")
    result
  }

  def getNetworksDetailedView: Seq[NetworkView] = {
    val start = System.currentTimeMillis()
    logger.info("Request to fetch networks details from organization " + vCloudAdapter.orgName)
    val vApps = vCloudAdapter.vappRefs.map(vAppRef => vCloudAdapter.vappByName(vAppRef.getName))
    val result = for (networkViewRef <- getNetworksRefsView) yield toView(vCloudAdapter.networkByName(networkViewRef.name), vApps)
    logger.info("Request to fetch networks details time: " + (System.currentTimeMillis() - start) + " ms")
    result
  }

}



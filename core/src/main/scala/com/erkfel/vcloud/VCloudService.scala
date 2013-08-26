package com.erkfel.vcloud

import scala.collection.mutable
import com.erkfel.model.{NetworkViewRef, NetworkView}
import com.erkfel.properties.VCloudPropertiesLoader._

/**
 * @author Oleg Aravin
 */
trait VCloudService {

  /**
   * The collection with networks view references will be returned, the reference means that network
   * contains minimum details, without detailed information about the VApps and IPranges. The reason
   * is to provide the user with the list of available network very quickly and make separate
   * time consuming requests by #getNetworkDetailedView and #getNetworksDetailedView.
   *
   * @return sequence with networks references view available in that vCloud service and organisation
   */
  def getNetworksRefsView: Seq[NetworkViewRef]

  /**
   * The collection with networks detailed view will be returned, including information about
   * attached vApps and network IP ranges.
   *
   * @param networkViewRef network reference
   * @return network detailed view available in that vCloud service and organisation
   */
  def getNetworkDetailedView(networkViewRef: NetworkViewRef): NetworkView

  /**
   * The collection with networks detailed view will be returned, including information about
   * attached vApps and network IP ranges.
   *
   * @return sequence with networks detailed view available in that vCloud service and organisation
   */
  def getNetworksDetailedView: Seq[NetworkView]

}

object VCloudService {

  // todo introduce weak reference
  private val vCloudServiceCache = new mutable.HashMap[String, VCloudServiceImpl]()

  def apply(orgName: String) = vCloudServiceCache
    .getOrElseUpdate(orgName, new VCloudServiceImpl(new VCloudClientAdapter(getProperties(orgName))))

}


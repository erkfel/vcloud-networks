package com.erkfel.vcloud

import org.slf4j.LoggerFactory
import com.vmware.vcloud.sdk._
import com.vmware.vcloud.sdk.constants.Version
import com.erkfel.model._
import com.vmware.vcloud.api.rest.schema.ReferenceType
import scala.collection.JavaConversions._
import com.erkfel.model.VappView
import com.erkfel.model.IPRange
import com.erkfel.model.NetworkView
import scala.Some

/**
 * @author Oleg Aravin
 */
class VCloudClientAdapter(clientProperties: VCloudClientProperties) {

  val logger = LoggerFactory.getLogger(classOf[VCloudClientAdapter])

  logger.info("Initializing vCloud client for the organization " + orgName)
  var vCloudClient = new VcloudClient(clientProperties.url, Version.V1_5)
  vCloudClient.registerScheme("https", 443, FakeSSLSocketFactory.getInstance())
  vCloudClient.login(clientProperties.username, clientProperties.password)
  val vdc = findVdc(clientProperties.orgName, clientProperties.vdcName)
  logger.info("vCloud client for the organization " + orgName + " has been successfully initialized")

  def findVdc(orgName: String, vdcName: String): Vdc = {
    val orgRef = vCloudClient.getOrgRefsByName.get(orgName)
    val org = Organization.getOrganizationByReference(vCloudClient, orgRef)
    val vdcRef = org.getVdcRefByName(vdcName)
    Vdc.getVdcByReference(vCloudClient, vdcRef)
  }

  def networkRefs: Seq[ReferenceType] = vdc.getAvailableNetworkRefs.toSeq

  def vappRefs: Seq[ReferenceType] = vdc.getVappRefs.toSeq

  def vappByName(name: String): Vapp = Vapp.getVappByReference(vCloudClient, vdc.getVappRefByName(name))

  def networkByName(name: String): OrgNetwork = OrgNetwork.getOrgNetworkByReference(vCloudClient, vdc.getAvailableNetworkRefByName(name))

  def orgName = clientProperties.orgName

  override def finalize() = logger.info("Destroying vCloud client for the organization " + orgName)
}

object VCloudClientAdapter {

  def getIPRange(network: OrgNetwork): Option[IPRange] = {
    network.getResource.getConfiguration.getIpScope.getIpRanges.getIpRange.headOption match {
      case Some(range) => Some(IPRange(IP(range.getStartAddress), IP(range.getEndAddress)))
      case _ => None
    }
  }

  def toView(network: OrgNetwork, vApps: Iterable[Vapp]): NetworkView = NetworkView(
    network.getResource.getName,
    vApps.filter(vapp => vapp.getNetworkNames.contains(network.getResource.getName)).map(toView).toSeq,
    getIPRange(network)
  )

  def toView(vapp: Vapp): VappView = {
    val vAppNetworkMap = collection.mutable.Map[String, List[IP]]()
    val networkIpBindings = vapp.getChildrenVms.map(_.getNetworkConnections.filter(_.getIpAddress != null)).flatten.map(
      connection => (connection.getNetwork, List(IP(connection.getIpAddress)))
    )
    networkIpBindings.foreach {
      case (network, ips) => vAppNetworkMap.get(network) match {
        case Some(l) => vAppNetworkMap.update(network, l ++ ips)
        case _ => vAppNetworkMap.update(network, ips)
      }
    }
    VappView(vapp.getReference.getName, vAppNetworkMap.toMap)
  }
}

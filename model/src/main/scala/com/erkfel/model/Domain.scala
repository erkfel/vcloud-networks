package com.erkfel.model


case class VappView(name: String, vAppIPsPerNetwork: Map[String, List[IP]])

case class IPRange(start: IP, end: IP)

case class VCloudClientProperties(url: String, username: String, password: String, orgName: String, vdcName: String)

case class NetworkViewRef(name: String)

case class NetworkView(name: String, vApps: Seq[VappView] = Seq.empty[VappView], ipRange: Option[IPRange] = None) {

  val usedIPs: Seq[IP] = vApps
    .filter(vAppView => vAppView.vAppIPsPerNetwork.get(name).isDefined)
    .map(vAppView => vAppView.vAppIPsPerNetwork(name)).flatten
    .sortWith(_ < _)

}

object SearchMode extends Enumeration {
  type SearchMode = Value
  val All, Free, Busy = Value
}

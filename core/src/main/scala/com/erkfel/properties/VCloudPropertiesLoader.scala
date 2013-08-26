package com.erkfel.properties

import com.erkfel.model.VCloudClientProperties
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory

/**
 * @author Oleg Aravin
 */
object VCloudPropertiesLoader {

  val logger = LoggerFactory.getLogger("VCloudPropertiesLoader")
  val properties = load()

  def load(): Seq[VCloudClientProperties] = {
    logger.info("Loading the configuration...")
    val conf = ConfigFactory.load().getConfig("vcloud")

    val result = for (orgConf <- conf.entrySet().map(_.getKey.split("\\.").head).map(conf.getConfig)) yield {
      logger.info("Load the configuration for the organization " + orgConf.getString("org"))
      VCloudClientProperties(
        orgConf.getString("url"),
        orgConf.getString("username") + "@" + orgConf.getString("org"),
        orgConf.getString("password"),
        orgConf.getString("org"),
        orgConf.getString("vdc")
      )
    }
    logger.info("Successfully load the configuration for " + result.size + " vCloud organizations")
    result.toSeq
  }

  def getProperties(orgName: String): VCloudClientProperties = properties.find(_.orgName.equals(orgName)) match {
    case Some(orgProps) => orgProps
    case other => throw new IllegalArgumentException("Unknown organization: " + other)
  }

}

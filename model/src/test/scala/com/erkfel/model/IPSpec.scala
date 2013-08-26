package com.erkfel.model

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Oleg Aravin
 */
@RunWith(classOf[JUnitRunner])
class IPSpec extends FunSpec {

  describe("City Parser ") {

    it("should parse the city list from the file") {
      val ip1 = IP("192.168.0.1")
      val ip2 = IP("192.168.0.10")
      val ip3 = IP("192.164.0.64")
      val ip4 = IP("192.168.0.10")

      assert(ip2 > ip1, "ip2 > ip1")
      assert(ip1 > ip3, "ip3 > ip1")
      assert(ip2 > ip3, "ip3 > ip2")

      assert(ip2 == ip4, "ip2 == ip4")
      assert(!(ip1 == ip2), "!(ip1 == ip2)")

      assert(ip1 < ip2, "ip1 < ip2")
      assert(ip3 < ip1, "ip3 < ip1")
      assert(!(ip2 < ip2), "!(ip2 < ip2)")

    }
  }
}

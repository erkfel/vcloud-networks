package com.erkfel.properties

import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec
import org.junit.runner.RunWith

/**
 * @author Oleg Aravin
 */
@RunWith(classOf[JUnitRunner])
class VCloudPropertiesLoaderSpec extends FunSpec {

  describe("Properties loader") {

    it("should be able to load the vCloud properties") {
      val props = VCloudPropertiesLoader.load()
      assert(props.nonEmpty)
    }

  }
}

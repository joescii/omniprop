package com.joescii.omniprop
package providers

import org.scalatest._
import matchers._

class PropertyProviderSpecs extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  override def beforeAll = {
    PropertyProviders.stack = List()
  }

  "PropertyProviders" should {
    "cause gets to throw an exception if PropertyProviders has not been configured" in {
      intercept[InvalidConfigurationException](PropertiesExceptions.get("doesn't matter"))
    }

    "throw an exception if configure invoked with an empty List" in {
      intercept[InvalidConfigurationException](PropertyProviders.configure())
    }

    "throw an exception on the second call to configure" in {
      PropertyProviders.configure(SystemPropertyProvider)
      intercept[InvalidConfigurationException](PropertyProviders.configure(SystemPropertyProvider))
    }
  }
}

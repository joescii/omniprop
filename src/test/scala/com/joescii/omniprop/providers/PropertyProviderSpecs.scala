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
      intercept[InvalidConfigurationException](PropertyProviders.configure(List()))
    }

    "throw an exception on the second call to configure" in {
      PropertyProviders.configure(List(SystemPropertyProvider))
      intercept[InvalidConfigurationException](PropertyProviders.configure(List(SystemPropertyProvider)))
    }

    val key = "com.joescii.omniprop.providers.test"

    "prefer the property value given by an earlier entry in the stack" in {
      PropertyProviders.stack = List(
        MapPropertyProvider(key -> "First"),
        MapPropertyProvider(key -> "Second")
      )

      PropertiesExceptions.get(key) should equal ("First")
    }

    "resolve a property from the second Provider in a stack if the first does not provide a value" in {
      PropertyProviders.stack = List(
        MapPropertyProvider(),
        MapPropertyProvider(key -> "Second")
      )

      PropertiesExceptions.get(key) should equal ("Second")
    }
  }
}

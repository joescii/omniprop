package com.joescii.omniprop

import org.scalatest._
import org.scalatest.matchers._

object PropertiesOptionsSpec {
  object test extends StringProperty
}

class PropertiesOptionsSpec extends WordSpec with ShouldMatchers{
  "The PropertiesOption object" should {
    "return None for an undefined property value" in {
      PropertiesOptions.get("something not defined") should be (None)
    }

    """return Some("value") for our defined test property""" in {
      import PropertiesOptionsSpec.test
      System.setProperty(test.key, "value")
      PropertiesOptions.get(test.key) should be (Some("value"))
    }
  }
}

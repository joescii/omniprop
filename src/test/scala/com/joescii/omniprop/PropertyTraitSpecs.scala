package com.joescii.omniprop

import org.scalatest._
import org.scalatest.matchers._

package object test {
  object property extends StringProperty
}
object Test {
  object string extends StringProperty
  object int extends IntProperty
}

class PropertyTraitSpecs extends WordSpec with ShouldMatchers {
  "A property defined in a package object" should { "have the correct key" in {
    test.property.key should equal ("com.joescii.omniprop.test.property")
  }}

  "A property defined in a non-package object" should { "have the correct key" in {
    Test.string.key should equal ("com.joescii.omniprop.Test.string")
  }}

  "StringProperty.get" should {
    "except for undefined properties" in {
      intercept[UnresolvedPropertyException](Test.string.get)
    }

    "implicitly convert to a String and equal the defined value" in {
      System.setProperty(Test.string.key, "value")
      val v:String = Test.string
      System.clearProperty(Test.string.key)
      v should equal ("value")
    }
  }

  "IntProperty.get" should {
    "except for undefined properpties" in {
      intercept[UnresolvedPropertyException](Test.int.get)
    }

    "implicitly convert to an Int and equal the defined value" in {
      System.setProperty(Test.int.key, "42")
      val v:Int = Test.int
      System.clearProperty(Test.int.key)
      v should equal (42)
    }
  }
}

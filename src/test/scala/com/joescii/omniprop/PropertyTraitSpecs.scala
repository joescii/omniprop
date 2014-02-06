package com.joescii.omniprop

import org.scalatest._
import org.scalatest.matchers._

package object test {
  object property extends StringProperty
}
object Test {
  object string extends StringProperty
  object boolean extends BooleanProperty
  object int extends IntProperty
  object finiteDuration extends FiniteDurationProperty
}

class PropertyTraitSpecs extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  override def beforeAll = {
    import providers._
    PropertyProviders.stack = List(SystemPropertyProvider)
  }

  override def afterAll = {
    System.clearProperty(Test.string.key)
    System.clearProperty(Test.boolean.key)
    System.clearProperty(Test.int.key)
    System.clearProperty(Test.finiteDuration.key)
  }

  "A property defined in a package object" should { "have the correct key" in {
    test.property.key should equal ("com.joescii.omniprop.test.property")
  }}

  "A property defined in a non-package object" should { "have the correct key" in {
    Test.string.key should equal ("com.joescii.omniprop.Test.string")
  }}

  "StringProperty" should {
    "except for undefined properties" in {
      intercept[UnresolvedPropertyException](Test.string.get)
    }

    "implicitly convert to a String and equal the defined value" in {
      System.setProperty(Test.string.key, "value")
      val v:String = Test.string
      v should equal ("value")
    }
  }

  "BooleanProperty" should {
    "except for undefined properties" in {
      intercept[UnresolvedPropertyException](Test.boolean.get)
    }

    "except for non-boolean properties" in {
      System.setProperty(Test.boolean.key, "garbage")
      intercept[WrongValueTypeException](Test.boolean.get)
    }

    "implicitly convert to a Boolean and equal the defined value" in {
      System.setProperty(Test.boolean.key, "true")
      val v1:Boolean = Test.boolean
      v1 should equal (true)
    }
  }

  "IntProperty" should {
    "except for undefined properties" in {
      intercept[UnresolvedPropertyException](Test.int.get)
    }

    "except for non-int properties" in {
      System.setProperty(Test.int.key, "garbage")
      intercept[WrongValueTypeException](Test.int.get)
    }

    "implicitly convert to an Int and equal the defined value" in {
      System.setProperty(Test.int.key, "42")
      val v:Int = Test.int
      v should equal (42)
    }
  }

  "FiniteDurationProperty" should {
    import scala.concurrent.duration._

    "except for undefined properties" in {
      intercept[UnresolvedPropertyException](Test.finiteDuration.get)
    }

    "except for non-duration properties" in {
      System.setProperty(Test.finiteDuration.key, "garbage")
      intercept[WrongValueTypeException](Test.finiteDuration.get)
    }

    "implicitly convert to a FiniteDuration and equal the defined value" in {
      System.setProperty(Test.finiteDuration.key, "5 seconds")
      val v:FiniteDuration = Test.finiteDuration
      v should equal(5.seconds)
    }
  }
}

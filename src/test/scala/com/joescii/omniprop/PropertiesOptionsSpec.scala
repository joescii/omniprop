package com.joescii.omniprop

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object PropertiesOptionsSpec extends Properties("PropertiesOption") {
  object test extends StringProperty

  property("get(undefined value) == None") = forAll { k:String =>
    PropertiesOptions.get(test.key+"."+k) == None
  }

  property("""get(defined value) == Some("value")""") = forAll { (k:String, v:String) =>
    val key = test.key+"."+k
    System.setProperty(key, v)
    val value = PropertiesOptions.get(key)
    System.clearProperty(key)

    Some(v) == value
  }
}

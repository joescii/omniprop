package com.joescii.omniprop

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object PropertiesOptionsChecks extends Properties("PropertiesOption") {
  object test extends StringProperty

  def undef(k:String) = test.key+"."+k

  property("get(undefined value) == None") = forAll { k:String =>
    PropertiesOptions.get(undef(k)) == None
  }

  property("get(defined value) == Some(value)") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)
    val value = PropertiesOptions.get(key)
    System.clearProperty(key)

    Some(v) == value
  }

  // This should be sufficient.  Any extra testing is just retesting otherwise tested code
}

package com.joescii.omniprop

import org.scalacheck.Properties
import org.scalacheck.Prop._

object PropertiesExceptionsChecks extends Properties("PropertiesExceptions") {
  object test extends StringProperty

  def undef(k:String) = test.key+"."+k

  property("get(undefined value) throws UnresolvedProperty") = forAll { k:String =>
    throws(classOf[UnresolvedProperty])(PropertiesExceptions.get(undef(k)))
  }

  property("get(defined value) == value") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)
    val value = PropertiesExceptions.get(key)
    System.clearProperty(key)

    v == value
  }
}

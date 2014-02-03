package com.joescii.omniprop

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object PropertiesOptionsSpec extends Properties("PropertiesOption") {
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

  property("getInt(undefined value) == None") = forAll { k:String =>
    PropertiesOptions.getInt(undef(k)) == None
  }

  property("getInt(defined value) == Some(integer)") = forAll { (k:String, v:Int) =>
    val key = undef(k)
    System.setProperty(key, v.toString)
    val value = PropertiesOptions.getInt(key)
    System.clearProperty(key)

    Some(v) == value
  }

  property("getInt(non-int value) == Some(integer)") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)
    val value = PropertiesOptions.getInt(key)
    System.clearProperty(key)

    """^\d+?$""".r.findFirstIn(v) match {
      case Some(num) => true // v happens to be an integer, and we're not testing that case here.
      case _ => value == None
    }
  }
}

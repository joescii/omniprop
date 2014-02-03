package com.joescii.omniprop

import org.scalacheck.Properties
import org.scalacheck.Prop._

object PropertiesExceptionsChecks extends Properties("PropertiesExceptions") {
  object test extends StringProperty

  def undef(k:String) = test.key+"."+k

  property("get(undefined value) throws UnresolvedProperty") = forAll { k:String =>
    throws(classOf[UnresolvedPropertyException])(PropertiesExceptions.get(undef(k)))
  }

  property("get(defined value) == value") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)
    val value = PropertiesExceptions.get(key)
    System.clearProperty(key)

    v == value
  }

  property("getInt(undefined value) throws UnresolvedProperty") = forAll { k:String =>
    throws(classOf[UnresolvedPropertyException])(PropertiesExceptions.getInt(undef(k)))
  }

  property("getInt(defined value) == integer") = forAll { (k:String, v:Int) =>
    val key = undef(k)
    System.setProperty(key, v.toString)
    val value = PropertiesExceptions.getInt(key)
    System.clearProperty(key)

    v == value
  }

  property("getInt(non-int value) == Some(integer)") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)

    val check:Boolean = """^\d+?$""".r.findFirstIn(v) match {
      case Some(num) => true // v happens to be an integer, and we're not testing that case here.
      case _ => throws(classOf[WrongValueTypeException])(PropertiesExceptions.getInt(key))
    }

    System.clearProperty(key)

    check
  }
}

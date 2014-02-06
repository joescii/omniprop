package com.joescii.omniprop

import org.scalacheck._
import Prop._
import Arbitrary._

import scala.concurrent.duration._

object PropertiesExceptionsChecks extends Properties("PropertiesExceptions") {
  providers.PropertyProviders.stack = List(providers.SystemPropertyProvider)

  object test extends StringProperty

  def undef(k:String) = test.key+"."+k

  lazy val genFiniteDuration:Gen[FiniteDuration] = for {
    lengthPicker <- arbitrary[Long]
    unitPicker <- arbitrary[Int]
  } yield {
    val (length:Long, unit) = unitPicker % 7 match {
      case 0 => (lengthPicker % 106751L, DAYS)
      case 1 => (lengthPicker % 2562047L, HOURS)
      case 2 => (lengthPicker % 9223372036854775L, MICROSECONDS)
      case 3 => (lengthPicker % 9223372036854L, MILLISECONDS)
      case 4 => (lengthPicker % 153722867L, MINUTES)
      case 5 => (lengthPicker % 9223372036854775807L, NANOSECONDS)
      case _ => (lengthPicker % 9223372036L, SECONDS)
    }
    FiniteDuration(length, unit)
  }

  implicit lazy val arbFiniteDuration: Arbitrary[FiniteDuration] = Arbitrary(genFiniteDuration)

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

  property("getBoolean(undefined value) throws UnresolvedProperty") = forAll { k:String =>
    throws(classOf[UnresolvedPropertyException])(PropertiesExceptions.getBoolean(undef(k)))
  }

  property("getBoolean(defined value) == boolean") = forAll { (k:String, v:Boolean) =>
    val key = undef(k)
    System.setProperty(key, v.toString)
    val value = PropertiesExceptions.getBoolean(key)
    System.clearProperty(key)

    v == value
  }

  property("getBoolean(non-boolean value) throws WrongValueTypeException") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)

    val check:Boolean = (v == "false") || (v == "true") || throws(classOf[WrongValueTypeException])(PropertiesExceptions.getBoolean(key))

    System.clearProperty(key)

    check
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

  property("getInt(non-int value) throws WrongValueTypeException") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)

    val check:Boolean = """^\d+?$""".r.findFirstIn(v) match {
      case Some(num) => true // v happens to be an integer, and we're not testing that case here.
      case _ => throws(classOf[WrongValueTypeException])(PropertiesExceptions.getInt(key))
    }

    System.clearProperty(key)

    check
  }

  property("getFiniteDuration(undefined value) throws UnresolvedProperty") = forAll { k:String =>
    throws(classOf[UnresolvedPropertyException])(PropertiesExceptions.getFiniteDuration(undef(k)))
  }

  property("getFiniteDuration(defined value) == FiniteDuration") = forAll { (k:String, d:FiniteDuration) =>
    val key = undef(k)
    System.setProperty(key, d.toString)
    val value = PropertiesExceptions.getFiniteDuration(key)
    System.clearProperty(key)

    d == value
  }

  property("getFiniteDuration(non-duration value) throws WrongValueTypeException") = forAll { (k:String, v:String) =>
    val key = undef(k)
    System.setProperty(key, v)
    // There is a slight chance that v happens to be a valid duration...
    val check = throws(classOf[WrongValueTypeException])(PropertiesExceptions.getFiniteDuration(key))
    System.clearProperty(key)

    check
  }
}

import org.scalatest._
import matchers.ShouldMatchers

class PropertiesExceptionsSpecs extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  import providers._

  "PropertiesExceptions" should {
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

    "not read beyond the first property provider which resolves the requested property" in {
      PropertyProviders.stack = List(
        MapPropertyProvider(),
        MapPropertyProvider(key -> "Second"),
        new PropertyProvider {
          override def get(key: String): Option[String] = throw new java.lang.Exception("Can't touch this!!")
        }
      )

      PropertiesExceptions.get(key) should equal ("Second")
    }
  }
}
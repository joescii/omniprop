package com.joescii

import scala.concurrent.duration.FiniteDuration

/** Import this package where you want to do property stuff. */
package object omniprop {
  /** Base property trait */
  trait Property[T] {
    lazy val key = this.getClass.getName.
      replaceAll("""\Q$\E""", ".").
      replaceAll("""\Q.package\E""", "").
      init

    /** Gets the configured value of this property */
    def get:T
  }

  /** Extend this class for a String-typed property */
  abstract class StringProperty extends Property[String] {
    lazy val get = PropertiesExceptions.get(key)
  }

  /** Extend this class for an Int-typed property */
  abstract class IntProperty extends Property[Int] {
    lazy val get = PropertiesExceptions.getInt(key)
  }

  /** Extend this class for a FiniteDuration-typed property */
  abstract class FiniteDurationProperty extends Property[FiniteDuration] {
    lazy val get = PropertiesExceptions.getFiniteDuration(key)
  }

  /** Optionally gets properties by name from the configured stack of PropertyProviders */
  object PropertiesOptions {
    private def toOption[T](getter: => T) = try {
      Some(getter)
    } catch {
      case _:Exception => None
    }

    /** Gets the property value from the stack of PropertyProviders */
    def get(key:String):Option[String] = toOption(PropertiesExceptions.get(key))

    /** Gets a property and converts the value to an integer */
    def getInt(key:String):Option[Int] = toOption(PropertiesExceptions.getInt(key))

    /** Gets a property and converts the value into a FiniteDuration object */
    def getFiniteDuration(key:String):Option[FiniteDuration] = toOption(PropertiesExceptions.getFiniteDuration(key))
  }

  /** Gets properties by name from the configured stack of PropertyProviders, throwing an exception for undefined properties */
  object PropertiesExceptions {
    import omniprop.providers.{ PropertyProvider, System => Sys }

    /** The stack of PropertyProviders to use for resolving property values.  */
    // TODO: Make this configurable, perhaps utilizing a Promise to allow it to be settable (once) and immutable
    private val providers:List[PropertyProvider] = List(Sys)

    /** Gets the property value from the stack of PropertyProviders */
    def get(key:String):String =
    // TODO: Map over the providers to resolve the property
      providers.head.get(key) match {
        case Some(v) => v
        case _ => throw UnresolvedPropertyException(key)
      }

    /** Gets a property and converts the value to an integer */
    def getInt(key:String):Int = {
      val v = get(key)
      try {
        Integer.parseInt(v)
      } catch {
        case _:NumberFormatException => throw WrongValueTypeException(key, v)
      }
    }

    /** Gets a property and converts the value into a FiniteDuration object */
    def getFiniteDuration(key:String):FiniteDuration = {
      val v = get(key)
      try {
        val split = v.split("""\s+""")
        FiniteDuration(java.lang.Long.parseLong(split(0)), split(1))
      } catch {
        case _:Exception => throw WrongValueTypeException(key, v)
      }
    }
  }

  /** Thrown when the requested property could not be resolved in the current configuration */
  case class UnresolvedPropertyException(key:String) extends Exception(key)
  /** Thrown when the requested property had a defined value which could not be converted to the expected type */
  case class WrongValueTypeException(key:String, value:String) extends Exception(key+" == '"+value+"'")

  implicit def ConvertString(p:StringProperty):String = p.get
  implicit def ConvertInt(p:IntProperty):Int = p.get
  implicit def ConvertFiniteDuration(p:FiniteDurationProperty):FiniteDuration = p.get
}

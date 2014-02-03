package com.joescii

/**
  *
  */
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

  /** Extend this trait for a String-typed property */
  trait StringProperty extends Property[String] {
    lazy val get = PropertiesExceptions.get(key)
  }

  /** Extend this trait for an Int-typed property */
  trait IntProperty extends Property[Int] {
    lazy val get = PropertiesExceptions.getInt(key)
  }

  /** Optionally gets properties by name from the configured stack of PropertyProviders */
  object PropertiesOptions {
    import omniprop.providers.{ PropertyProvider, System => Sys }

    /** The stack of PropertyProviders to use for resolving property values.  */
    // TODO: Make this configurable, perhaps utilizing a Promise to allow it to be settable (once) and immutable
    private val providers:List[PropertyProvider] = List(Sys)

    /** Gets the property value from the stack of PropertyProviders */
    def get(key:String):Option[String] =
      // TODO: Map over the providers to resolve the property
      providers.head.get(key)

    /** Gets a property and converts the value to an integer */
    def getInt(key:String):Option[Int] = get(key).flatMap { v =>
      try {
        Some(Integer.parseInt(v))
      } catch {
        case _:NumberFormatException => None
      }
    }
  }

  /** Gets properties by name from the configured stack of PropertyProviders, throwing an exception for undefined properties */
  object PropertiesExceptions {
    private def toException[T](getter: String => Option[T], key: String) = getter(key) match {
      case Some(v) => v
      case _ => throw new UnresolvedPropertyException(key)
    }

    def get(key:String):String = toException(PropertiesOptions.get, key)
    def getInt(key:String):Int = toException(PropertiesOptions.getInt, key)
  }

  case class UnresolvedPropertyException(key:String) extends Exception(key)
  case class WrongValueTypeException(key:String, value:String) extends Exception(key+" == '"+value+"'")

  implicit def ConvertString(p:StringProperty):String = p.get
  implicit def ConvertInt   (p:IntProperty)   :Int    = p.get
}

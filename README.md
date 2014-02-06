omniprop
========

Scala DSL for unifying JVM property libraries in a type-safe manner

Problem Statement
=================

This little project aims to solve the following problems:

1. Different libraries and frameworks have different APIs for doing the same thing. 
2. These aforementioned APIs have differing opinions on how to handle properties (exceptions vs `Option[String]`).
3. Properties are a `Map[String, String]`, and hence inherently type-unsafe.
4. Property usage can be difficult to track and locate in a large code base.
5. Property names can conflict without any warning.

This library solves the above problems by defining a common trait named `PropertyProvider` with only one method, `get(key:String):Option[String]`, for which each supported library will have an implementation.  (Currently supporting Java's `System`.  Coming soon: Lift's `Props` and Typesafe's `config` library).  These property providers can be accessed via either `PropertiesOptions` if you like providing defaults or `PropertiesExceptions` if you think unresolved properties should abort the program.  Both objects provide getters which convert the property into the expected type.  To help with naming, properties can be defined as objects extending the sub-classes of `Property[T]` such as `StringProperty`, `IntProperty`, and `FiniteDurationProperty`.  These objects assume their fully-qualified classname as the name of the property, minimizing the probability of naming conflicts and neatly tying the name of the property to the exact location of its usage in the code base.

For further reading, please see [my blog post](http://proseand.co.nz/2014/02/03/java-properties-made-elegant-in-scala/) where I first presented this approach to handling properties in a Scala application

Configuration
=============

Add the Sonatype.org Releases repo as a resolver in your `build.sbt` or `Build.scala` as appropriate.

```scala
resolvers += "Sonatype.org Releases" at "https://oss.sonatype.org/content/repositories/releases/"
```

Add **omniprop** as a dependency in your `build.sbt` or `Build.scala` as appropriate.

```scala
libraryDependencies ++= Seq(
  // Other dependencies ...
  "com.joescii" %% "omniprop" % "0.2" % "compile"
)
```

Scala Versions
==============

Unfortunately, only one Scala version is supported, namely 2.10 (2.10.3 in particular).  This library includes a `FiniteDurationProperty` for resolving properties of type `scala.concurrent.duration.FiniteDuration` which is only available in 2.9.3 and 2.10.x.  Then 2.9.3 is eliminated by the optional dependency on Lift 2.5 which is not currently compiled against 2.9.3.

Usage
=====

To use the **omniprop** DSL in your Scala application, you will need to do the following:

1. Configure which property providers are to be utilized by calling `com.joescii.omniprop.providers.PropertyProviders.configure(/* ... */)`.  This must be done before any properties are read, so place it in your application bootstrap.
2. Import `com.joescii.omniprop._`
3. Define an `object` which extends the appropriately-typed sub class of `Property[T]`
4. On the property object, either explicitly call `get` or implicitly convert to the value type

Exception Handling
==================

By design, you should never catch any of the exceptions thrown by **omniprop**.  They are informing you that your application is misconfigured and attempt to provide a hint for what is wrong.  An exception to this rule (no pun intended), is your test code.  Because `PropertyProviders.configure` should be invoked only once (to prevent clobbering, race conditions, and other mistakes) and test are run out of order by design, I find it necessary to wrap the call to `configure` in a `scala.util.Try` and have every test suite invoke the configuration before the tests run.

Examples
========

Configuration for Lift

```scala
package bootstrap.liftweb

class Boot {
  def boot {
    import com.joescii.omniprop.providers._
    PropertyProviders.configure(
      SystemPropertyProvider, // First check SystemProperties
      LiftPropsProvider       // If not found there, check Lift's Props
    )
    
    // ...
  }
}
```

Define properties and use them

```scala
package org.example

import com.joescii.omniprop._

// Not required to be defined in an object, but ideal as it will only be read once.
object MyClass {
  object myprop extends StringProperty
}
class MyClass {
  import MyClass._
  
  // Explicit access
  val myExpVal = myprop.get
  
  // Implicit conversion
  val myImpVal:String = myprop
}
```

TODO
====

I have these features planned for version 1.0

1. Support default values for `Property[T]` objects.
2. Support Typesafe `config`.

Maybe one day

1. Configure whether the property should be read each time (i.e. `def`) or read once (i.e. `lazy val`).  Current behavior is the latter.
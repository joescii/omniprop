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

Usage
=====

To use the **omniprop** DSL in your Scala application, you will need to do the following:

1. Import `com.joescii.omniprop._`
2. Define an `object` which extends the appropriately-typed sub class of `Property[T]`
3. On the property object, either explicitly call `get` or implicitly convert to the value type

Examples
========

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

1. Support configuring `PropertyProvider` stack.
2. Support Lift `Props`.
3. Support Typesafe `config`.
4. Support default values for `Property[T]` objects.

Maybe one day

1. Configure whether the property should be read each time (i.e. `def`) or read once (i.e. `lazy val`).  Current behavior is the latter.
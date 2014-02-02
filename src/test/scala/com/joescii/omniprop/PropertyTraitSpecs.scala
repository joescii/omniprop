package com.joescii.omniprop

import org.scalatest._
import org.scalatest.matchers._

package object test {
  object property extends StringProperty
}
object Test {
  object property extends StringProperty
}

class PropertyTraitSpecs extends FlatSpec with ShouldMatchers {
  "A property defined in a package object" should "have the correct key" in {
    test.property.key should equal ("com.joescii.omniprop.test.property")
  }

  "A property defined in a non-package object" should "have the correct key" in {
    Test.property.key should equal ("com.joescii.omniprop.Test.property")
  }
}

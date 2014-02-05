package com.joescii.omniprop
package providers

/** Base trait for objects which can provide property values */
trait PropertyProvider {
  def get(key:String):Option[String]
}

object PropertyProviders {
  private[omniprop] var stack:List[PropertyProvider] = List(SystemPropertyProvider)
}
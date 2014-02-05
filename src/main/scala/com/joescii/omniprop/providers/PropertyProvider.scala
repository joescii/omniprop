package com.joescii.omniprop
package providers

/** Base trait for objects which can provide property values */
trait PropertyProvider {
  def get(key:String):Option[String]
}

object PropertyProviders {
  self =>

  private[omniprop] var stack:List[PropertyProvider] = List()

  def configure(stack:List[PropertyProvider]) = {
    if(stack.isEmpty)       throw InvalidConfigurationException("PropertyProviders.configure must be invoked with a non-empty list")
    if(!self.stack.isEmpty) throw InvalidConfigurationException("PropertyProviders.configure must be invoked only once")

    self.stack = stack
  }
}
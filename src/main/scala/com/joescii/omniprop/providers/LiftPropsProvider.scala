package com.joescii.omniprop
package providers

import net.liftweb.util.Props

object LiftPropsProvider extends PropertyProvider {
  def get(key:String) = Props.get(key)
}

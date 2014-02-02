package com.joescii.omniprop.providers

/** Base trait for objects which can provide property values */
trait PropertyProvider {
  def get(key:String):Option[String]
}

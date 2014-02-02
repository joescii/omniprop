package com.joescii.omniprop.providers

import scala.sys.SystemProperties

/** Provides properties from the JVM's System properties object */
object System extends PropertyProvider {
  private val props = new SystemProperties()

  def get(key:String) = props.get(key)
}

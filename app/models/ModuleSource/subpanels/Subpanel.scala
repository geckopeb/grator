package it.grator.module_source.subpanels

import it.grator.module_source.fields.RelatedField
import it.grator.module_source.Module

case class Subpanel(
  name: String,
  toModule: Module,
  fromModule: Module,
  fromField: RelatedField
){
  def controllerName: String = {
    val tName = name.capitalize
    s"""subpanel$tName"""
  }
}

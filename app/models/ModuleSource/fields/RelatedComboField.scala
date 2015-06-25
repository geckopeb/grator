package it.grator.module_source.fields

import it.grator.module_source._

import it.grator.utils.TextUtils

case class RelatedComboField(
  name: String,
  required: Boolean,
  module: Module,
  relatedModule: Module
) extends RelatedField{
  override def htmlForm(app: App): String = {
    val varOrTuple = this.module.varOrTuple(app)
    val relatedModuleName = this.relatedModule.name
    val tuple = module.tupleExtractor(app)
    
    s"""
      @$varOrTuple match{
        case Some($tuple) => {
          @views.html.$relatedModuleName.widgets.related_combo("$name", rowForm("$name"), Some($name))
        }
        case None => {
          @views.html.$relatedModuleName.widgets.related_combo("$name", rowForm("$name"), None)
        }
      }
    """
  }
}
package it.grator.module_source.fields

import it.grator.module_source._

import it.grator.utils.TextUtils

case class RelatedDropdownField(
	name: String,
	required: Boolean,
	module: Module,
	relatedModule: Module
) extends RelatedField{
	override def htmlForm(app: App): String = {
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		   @select(
		   		rowForm("$name"),
		   		models.DB.$relatedClassName.getOptions,
		   		'default -> "-- select --"
		   )"""
	}
}
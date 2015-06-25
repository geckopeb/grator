package it.grator.module_source.fields

import it.grator.module_source._

case class NameField(
	name: String,
	required: Boolean,
	module: Module
) extends Field{
	def formType: String = "nonEmptyText"

	override def controllerForm: String = {
		s""""name" -> $formType"""
	}

	override def htmlForm(app: App): String = {
		s"""<legend>
				@Messages("$moduleName.item_name")
		   </legend>
		    @helper.inputText(rowForm("name"))"""
	}

	def fieldTable: String = {
		"""def name = column[String]("name")"""
	}

	def fieldType: String = "String"
}

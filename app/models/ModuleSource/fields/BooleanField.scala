package it.grator.module_source.fields

import it.grator.module_source.Module

case class BooleanField(
	name: String,
	required: Boolean,
	module: Module
) extends Field{
	def formType = "boolean" //ESP: que un boolean sea requerido implica que debe marcarse siempre.
	def fieldType: String = "Boolean"

	override def controllerForm: String = {
		s""""$name" -> $formType"""
	}

	override def htmlForm: String = {
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		 @helper.checkbox(
	      rowForm("$name")
	    )"""
	}

	def fieldTable: String = {
		s"""def $name = column[Boolean]("$tableName")"""
	}

	override def nameInTable: String = this.name

	override def classDefinition: String = {
		s"""$name: $fieldType"""
	}
}
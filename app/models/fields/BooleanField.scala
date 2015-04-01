package models.fields

import models.DB.GratorField

class BooleanField(val field: GratorField) extends Field{
	def controllerForm: String = {
		//val formType = if(field.required) "boolean" else "optional(boolean).getOrElse(false)"
		val formType = "boolean"
		"\""+field.name+"\" -> "+formType
	}

	override def htmlForm: String = {
		val name = field.name
		val moduleName = field.module.name
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		 @helper.checkbox(
	      rowForm("$name")
	    )"""
	}

	def fieldTable: String = {
		val tableName = this.tableName
		//val required = if(field.required){", O.NotNull"} else {""}
		s"""def $name = column[Boolean]("$tableName")"""
	}

	override def nameInTable: String = {
		field.name
	}

	def fieldType: String = "Boolean"

	override def classDefinition: String = {
		val name = this.field.name
		val fieldType = this.fieldType
		s"""$name: $fieldType"""
	}
}
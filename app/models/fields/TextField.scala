package models.fields

import models.DB.FieldRow

class TextField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "nonEmptyText" else "optional(text)"
		"\""+field.name+"\" -> "+formType
	}

	def fieldTable: String = {
		val name = field.name
		val required = if(field.required){", O.NotNull"} else {""}
		s"""def $name = column[String]("$name"$required)"""
	}

	def fieldType: String = "String"
}
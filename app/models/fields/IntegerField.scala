package models.fields

import models.DB.FieldRow

class IntegerField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "number" else "optional(number)"
		"\""+field.name+"\" -> "+formType
	}

	def fieldTable: String = {
		val name = field.name
		val required = if(field.required){", O.NotNull"} else {""}
		s"""def $name = column[Int]("$name"$required)"""
	}

	def fieldType: String = "Int"
}
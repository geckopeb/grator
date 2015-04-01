package models.fields

import models.DB.GratorField

class IntegerField(val field: GratorField) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "number" else "optional(number)"
		"\""+field.name+"\" -> "+formType
	}

	def fieldTable: String = {
		val tableName = this.tableName
		val required = if(field.required){", O.NotNull"} else {", O.Nullable"}
		s"""def $name = column[Int]("$tableName"$required)"""
	}

	def fieldType: String = "Int"
}
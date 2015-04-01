package models.fields

import models.DB.GratorField

class TextField(val field: GratorField) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "nonEmptyText" else "optional(text)"
		"\""+field.name+"\" -> "+formType
	}

	def fieldTable: String = {
		val tableName = this.tableName
		val required = if(field.required){", O.NotNull"} else {", O.Nullable"}
		s"""def $name = column[String]("$tableName"$required)"""
	}

	def fieldType: String = "String"
}
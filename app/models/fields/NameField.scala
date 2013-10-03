package models.fields

import models.DB.FieldRow

class NameField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		"\"name\" -> nonEmptyText"
	}

	def htmlForm: String = {
		val moduleName = field.module.name
		s"""<legend>
				@Messages("$moduleName.item_name")
		   </legend>
		    @helper.inputText(rowForm("name"))"""
	}

	def fieldTable: String = {
		"def name = column[String](\"name\",O.NotNull)"
	}

	def nameInTable: String = {
		"name"
	}

	def list: String = {
		"@row.name"
	}
}
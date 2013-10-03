package models.fields

import models.DB.FieldRow

class TextField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "nonEmptyText" else "text"
		"\""+field.name+"\" -> "+formType
	}

	def htmlForm: String = {
		""
	}

	def fieldTable: String = {
		""
	}
	
	def nameInTable: String = {
		""
	}

	def list: String = {
		""
	}
}
package models.fields

import models.DB.FieldRow

class BooleanField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "boolean" else "optional(boolean)"
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
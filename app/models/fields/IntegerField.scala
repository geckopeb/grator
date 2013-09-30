package models.fields

import models.DB.FieldRow

class IntegerField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "number" else "optional(number)"
		"\""+field.name+"\" -> "+formType
	}

	def htmlForm: String = {
		""
	}
}
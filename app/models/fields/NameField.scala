package models.fields

import models.DB.FieldRow

class NameField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		"\"name\" -> nonEmptyText"
	}
}
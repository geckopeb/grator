package models.fields

import models.DB.FieldRow

class IdField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		"\"id\" -> optional(longNumber)"
	}

	def htmlForm: String = {
 """@row match {
    	case Some(row) => {
    		<input type="hidden" name="id" value="@row.id.get">
    	}
    	case None => {}
	  }"""
	}
}
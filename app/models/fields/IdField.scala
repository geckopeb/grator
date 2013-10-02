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

	def fieldTable: String = {
		"def id = column[Long](\"id\", O.PrimaryKey, O.AutoInc)"
	}

	def nameInTable: String = {
		"id.?"	
	}
}
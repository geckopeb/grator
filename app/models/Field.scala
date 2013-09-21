package models

import models.DB.FieldRow
import models.DB.FieldTable

class Field(currentRow: FieldRow){
  val row = currentRow
}

object Field{
   def findByModule(moduleId: Long): List[Field] = {
    for(row <- FieldRow.findByModule(moduleId)) yield(new Field(row))
  }
}
package models

import models.DB.ModuleRow
import models.DB.ModuleTable

class Module(currentRow: ModuleRow){
  val row = currentRow
  val fields = this.row.id match{
    case Some(id) => Field.findByModule(id)
    case None => List()
  }
}

object Module{
  def findByApplication(applicationId: Long): List[Module] = {
    for(row <- ModuleRow.findByApplication(applicationId)) yield(new Module(row))
  }
}
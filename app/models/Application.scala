package models

import models.DB.ApplicationRow
import models.DB.ApplicationTable

import utils.persistence.RowManager

class Application(currentRow: ApplicationRow) extends RowManager{
  val row = currentRow
  val modules = this.row.id match {
    case Some(id) => Module.findByApplication(id)
    case None => List()
  }
}
object Application{

}
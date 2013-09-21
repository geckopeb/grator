
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

case class FieldRow(
  id: Option[Long] = None,
  name: String,
  moduleId: Long,
  fieldType: String,
  required: Boolean
){
  
}

object FieldRow{
  def save(field: FieldRow):Long = {
    DB.withTransaction { implicit session =>
      FieldTable.returning(FieldTable.id).insert(field)
    }
  }
  
  def update(field: FieldRow):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- FieldTable
        if s.id === field.id
      } yield(s)
      q.update(field)
    }
  }
  
  def delete(field: FieldRow):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- FieldTable
        if s.id === field.id.get
      } yield(s)
      (new DeleteInvoker(q)).delete
    }
  }

  
  def findAll: List[FieldRow] = {
    DB.withSession { implicit session =>
      Query(FieldTable).list
    }
  }
  
  def findById(id: Long):Option[FieldRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- FieldTable if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val fields = for {
        p <- FieldTable
      } yield(p)
      for(field <- fields.list) yield(field.id.get.toString,field.name) 
    }
  }

  def findByModule(moduleId: Long): List[FieldRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- FieldTable if s.moduleId === moduleId
      } yield (s)
      q.list
    }
  }

  def getTypeOptions(): Seq[(String,String)] = {
    Seq(("Text","String"),("Number","Int"),("Boolean","Boolean"))
  }
}
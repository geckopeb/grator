
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker

import models.Module
import models.DB.ModuleRow

case class FieldRow(
  id: Option[Long] = None,
  name: String,
  moduleId: Long,
  fieldType: String,
  required: Boolean,
  relatedModuleId: Option[Long] = None
){
  lazy val module = ModuleRow.findById(this.moduleId).get
  lazy val moduleModule = new Module(this.module)

  lazy val relatedModule: Option[ModuleRow] = {
    this.relatedModuleId match{
      case Some(id) => Some(ModuleRow.findById(id).get)
      case None => None
    }
  }

  lazy val relatedModuleModule: Option[Module] = this.relatedModule match {
    case Some(moduleRow) => Some(new Module(moduleRow))
    case None => None
  }
  
}

object FieldRow{
  def save(field: FieldRow):Long = {
    DB.withTransaction { implicit session =>
      FieldTable.fieldTable.returning(FieldTable.fieldTable.map(_.id)).insert(field)
    }
  }
  
  def update(field: FieldRow):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- FieldTable.fieldTable
        if s.id === field.id
      } yield(s)
      q.update(field)
    }
  }
  
  def delete(field: FieldRow):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- FieldTable.fieldTable
        if s.id === field.id.get
      } yield(s)
      q.delete
    }
  }

  
  def findAll: List[FieldRow] = {
    DB.withSession { implicit session =>
      FieldTable.fieldTable.run.toList
    }
  }
  
  def findById(id: Long):Option[FieldRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- FieldTable.fieldTable if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val fields = for {
        p <- FieldTable.fieldTable
      } yield(p)
      for(field <- fields.list) yield(field.id.get.toString,field.name) 
    }
  }

  def findByModule(moduleId: Long): List[FieldRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- FieldTable.fieldTable if s.moduleId === moduleId
      } yield (s)
      q.list
    }
  }

  def getTypeOptions(): Seq[(String,String)] = {
    Seq(
      //("Id","Id"),
      //("Name","Name"),
      ("Text","Text"),
      ("Integer","Integer"),
      ("Boolean","Boolean"),
      ("Related","Related")
    )
  }
}
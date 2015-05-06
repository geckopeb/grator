package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import GratorApp.gratorAppT

import utils.FileUtils

import play.api.Logger

case class GratorModule(
    id: Option[Long] = None,
    name: String,
    applicationId: Long
){

}

object GratorModule{
  
  class GratorModuleT(tag: Tag) extends Table[GratorModule](tag, "grator_module"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name",O.NotNull)
    def applicationId = column[Long]("application_id", O.NotNull)

    def * = (id.?, name, applicationId ) <> ((GratorModule.apply _).tupled, GratorModule.unapply _)
    
    def applicationIdId = foreignKey("grator_module_grator_app_application_id", applicationId, GratorApp.gratorAppT)(_.id)

  }

  val gratorModuleT = TableQuery[GratorModuleT]

  def save(gratorModule: GratorModule):Long = {
    DB.withTransaction { implicit session =>
      val id = this.gratorModuleT.returning(this.gratorModuleT.map(_.id)).insert(gratorModule)

      //The final parameter should be none, but then joins do not work properly. slick3 will provide a solution.
      //val moduleId = new GratorField(None, "id", id, "Id", true, None)
      val moduleId = new GratorField(None, "id", id, "Id", true, Some(id))
      GratorField.gratorFieldT.insert(moduleId)

      //The final parameter should be none, but then joins do not work properly. slick3 will provide a solution.
      //val moduleName = new GratorField(None, "name", id, "Name", true, None)
      val moduleName = new GratorField(None, "name", id, "Name", true, Some(id))
      GratorField.gratorFieldT.insert(moduleName)
      
      id
    }
  }
  
  def update(gratorModule: GratorModule):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- this.gratorModuleT
        if s.id === gratorModule.id
      } yield(s)
      q.update(gratorModule)
    }
  }
  
  def delete(gratorModule: GratorModule):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- this.gratorModuleT
        if s.id === gratorModule.id.get
      } yield(s)
      q.delete
    }
  }

  def findAllWithRelateds: List[( GratorModule , GratorApp )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorModule <- this.gratorModuleT
        gratorAppapplicationId <- gratorAppT if gratorModule.applicationId === gratorAppapplicationId.id

      } yield (gratorModule , gratorAppapplicationId)
      q.list
    }
  }
  
  def findAll: List[GratorModule] = {
    DB.withSession { implicit session =>
      this.gratorModuleT.list
    }
  }
  
  def findById(id: Long):Option[GratorModule] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- this.gratorModuleT if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def findByIdWithRelateds(id: Long):Option[( GratorModule , GratorApp )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorModule <- this.gratorModuleT if gratorModule.id === id
        gratorAppapplicationId <- gratorAppT if gratorModule.applicationId === gratorAppapplicationId.id

      } yield (gratorModule , gratorAppapplicationId)
      q.firstOption
    }
  }

  def findByApplicationWithRelateds(applicationId: Long):List[( GratorModule , GratorApp )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorModule <- this.gratorModuleT if gratorModule.applicationId === applicationId
        gratorAppapplicationId <- gratorAppT if gratorModule.applicationId === gratorAppapplicationId.id

      } yield (gratorModule , gratorAppapplicationId)
      q.list
    }
  }

  def findByApplication(applicationId: Long): List[GratorModule] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- GratorModule.gratorModuleT if s.applicationId === applicationId
      } yield (s)
      q.list
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val gratorModules = for {
        p <- this.gratorModuleT
      } yield(p)
      for(gratorModule <- gratorModules.list) yield(gratorModule.id.get.toString,gratorModule.name) 
    }
  }
}
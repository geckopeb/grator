package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import GratorModule.gratorModuleT

//import models.Module
import models.DB.GratorModule

case class GratorField(
    id: Option[Long] = None,
    name: String,
    moduleId: Long,
    fieldType: String,
    required: Boolean,
    relatedModuleId: Option[Long]
){
  def module = GratorModule.findById(this.moduleId).get

  def relatedModule: Option[GratorModule] = {
    this.relatedModuleId match{
      case Some(id) => Some(GratorModule.findById(id).get)
      case None => None
    }
  }
}

object GratorField{
  
  class GratorFieldT(tag: Tag) extends Table[GratorField](tag, "grator_field"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name",O.NotNull)
    def moduleId = column[Long]("module_id", O.NotNull)
    def fieldType = column[String]("field_type", O.NotNull)
    def required = column[Boolean]("required")
    def relatedModuleId = column[Long]("related_module_id", O.Nullable)

    def * = (id.?, name, moduleId, fieldType, required, relatedModuleId.? ) <> ((GratorField.apply _).tupled, GratorField.unapply _)
    
    def moduleIdId = foreignKey("grator_field_grator_module_module_id", moduleId, GratorModule.gratorModuleT)(_.id)
    def relatedModuleIdId = foreignKey("grator_field_grator_module_related_module_id", relatedModuleId, GratorModule.gratorModuleT)(_.id)

  }

  val gratorFieldT = TableQuery[GratorFieldT]

  def save(gratorField: GratorField):Long = {
    DB.withTransaction { implicit session =>
      this.gratorFieldT.returning(this.gratorFieldT.map(_.id)).insert(gratorField)
    }
  }
  
  def update(gratorField: GratorField):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- this.gratorFieldT
        if s.id === gratorField.id
      } yield(s)
      q.update(gratorField)
    }
  }
  
  def delete(gratorField: GratorField):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- this.gratorFieldT
        if s.id === gratorField.id.get
      } yield(s)
      q.delete
    }
  }

  def findAllWithRelateds: List[( GratorField , GratorModule, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorField <- this.gratorFieldT
        gratorModulemoduleId <- gratorModuleT if gratorField.moduleId === gratorModulemoduleId.id
        gratorModulerelatedModuleId <- gratorModuleT if gratorField.relatedModuleId === gratorModulerelatedModuleId.id

      } yield (gratorField , gratorModulemoduleId, gratorModulerelatedModuleId)
      q.list
    }
  }
  
  def findAll: List[GratorField] = {
    DB.withSession { implicit session =>
      this.gratorFieldT.list
    }
  }
  
  def findById(id: Long):Option[GratorField] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- this.gratorFieldT if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def findByIdWithRelateds(id: Long):Option[( GratorField , GratorModule, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorField <- this.gratorFieldT if gratorField.id === id
        gratorModulemoduleId <- gratorModuleT if gratorField.moduleId === gratorModulemoduleId.id
        gratorModulerelatedModuleId <- gratorModuleT if gratorField.relatedModuleId === gratorModulerelatedModuleId.id

      } yield (gratorField , gratorModulemoduleId, gratorModulerelatedModuleId)
      q.firstOption
    }
  }

  def findByModuleIdWithRelateds(moduleId: Long):List[( GratorField , GratorModule, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorField <- this.gratorFieldT if gratorField.moduleId === moduleId
        gratorModulemoduleId <- gratorModuleT if gratorField.moduleId === gratorModulemoduleId.id
        gratorModulerelatedModuleId <- gratorModuleT if gratorField.relatedModuleId === gratorModulerelatedModuleId.id

      } yield (gratorField , gratorModulemoduleId, gratorModulerelatedModuleId)
      q.list
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val gratorFields = for {
        p <- this.gratorFieldT
      } yield(p)
      for(gratorField <- gratorFields.list) yield(gratorField.id.get.toString,gratorField.name) 
    }
  }

  def findByModule(moduleId: Long): List[GratorField] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- GratorField.gratorFieldT if s.moduleId === moduleId
      } yield (s)
      q.list
    }
  }

  def findByApplication(applicationId: Long): List[GratorField] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- GratorModule.gratorModuleT if s.applicationId === applicationId
        f <- GratorField.gratorFieldT if s.id === f.moduleId
      } yield (f)
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
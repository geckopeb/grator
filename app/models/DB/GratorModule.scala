package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import GratorApp.gratorAppT

import utils.FileUtils

import models.fields._
import models.fields.RelatedField
import models.Module
import models.Relationship

import play.api.Logger

case class GratorModule(
    id: Option[Long] = None,
    name: String,
    applicationId: Long
){
  lazy val fields = GratorField.findByModule(this.id.get)
  lazy val renderFields = for (field <- this.fields) yield (FieldFactory.get(field))
  lazy val application = GratorApp.findById(this.applicationId)

  lazy val prel = GratorRelationship.findByPrimaryModuleId(this.id.get)
  lazy val prelRel = prel.map(p => new Relationship(p._1)) 

  lazy val rrel = GratorRelationship.findByRelatedModuleId(this.id.get)


  lazy val relatedFields: List[RelatedField] = for{
    field <- this.fields
    if(field.fieldType == "Related")
  }  yield(new RelatedField(field))

  lazy val module = new Module(this)

  def getPath(folder: String, fileTermination: String): String = {
    val basePath = this.application.get.path

    val path = basePath+folder+this.name.capitalize+fileTermination
    Logger.debug(path)
    path
  }

  def getRelPath(folder: String, fileTermination: String, rel: Relationship): String = {
    val basePath = this.application.get.path

    val path = basePath+folder+rel.fileName+fileTermination
    Logger.debug(path)
    path
  }

  def getViewPath(viewName: String): String = {
    val app = GratorApp.findById(this.applicationId)
    val basePath = app.get.path

    basePath+"app/views/"+this.name+"/"+viewName+".scala.html"
  }

  def getRelViewPath(viewName: String, rel: Relationship): String = {
    val app = GratorApp.findById(this.applicationId)
    val basePath = app.get.path

    basePath+"app/views/"+this.name+"/subpanels/"+rel.rmVar+"/"+viewName+".scala.html"
  }

  def generateAll(): Unit = {
    this.generateController()
    this.generateRow()
    this.generateViews()
    this.generateRelationships()
  }

  def generateController(): Unit = {
    val path = this.getPath("app/controllers/","Controller.scala")
    FileUtils.writeToFile(path,views.html.gratorModule.template.controller(this.module,this.renderFields,this.relatedFields, this.prelRel).toString)
  }

  def generateRow(): Unit = {
    val path = this.getPath("app/models/DB/",".scala")
    FileUtils.writeToFile(path,views.html.gratorModule.template.row(this.module,this.renderFields, this.relatedFields).toString)
  }

  def generateDetailView(): Unit = {
    val path = this.getViewPath("detail")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.detail(this.module, this.relatedFields, this.prelRel).toString)
  }

  def generateEditView(): Unit = {
    val path = this.getViewPath("edit")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.edit(this.module).toString)
  }

  def generateFormView(): Unit = {
    val path = this.getViewPath("form")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.form(this.module,this.renderFields).toString)
  }

  def generateIndexView(): Unit = {
    val path = this.getViewPath("index")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.index(this.module, this.relatedFields).toString)
  }

  def generateInsertView(): Unit = {
    val path = this.getViewPath("insert")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.insert(this.module).toString)
  }

  def generateListView(): Unit = {
    val path = this.getViewPath("list")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.list(this.module,this.renderFields, this.relatedFields).toString)
  }

  def generateDeleteView(): Unit = {
    val path = this.getViewPath("delete")
    FileUtils.writeToFile(path,views.html.gratorModule.template.moduleviews.delete(this.module).toString)
  }

  def generateViews(): Unit = {
    this.generateDetailView()
    this.generateEditView()
    this.generateFormView()
    this.generateIndexView()
    this.generateInsertView()
    this.generateListView()
    this.generateDeleteView()
  }

  def generateRelationshipRow(rel: Relationship): Unit = {
    val path = this.getRelPath("app/models/DB/",".scala",rel)
    FileUtils.writeToFile(path,views.html.gratorRelationship.template.row(rel).toString)
  }

  def generateRelationshipViews(rel: Relationship): Unit = {
    val path = this.getRelViewPath("subpanel",rel)

    FileUtils.writeToFile(path,views.html.gratorRelationship.template.subpanel.subpanel_template(rel).toString) 

    val insertPath = this.getRelViewPath("insert",rel)
    FileUtils.writeToFile(insertPath,views.html.gratorRelationship.template.subpanel.insert(this.module,rel).toString) 
  }

  def generateRelationship(rel: Relationship): Unit = {
    this.generateRelationshipRow(rel)
    this.generateRelationshipViews(rel)
  }

  def generateRelationships(): Unit = {
    for (relRow <- this.prel){
      val rel = new Relationship(relRow._1)
      this.generateRelationship(rel)
    }
  }
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
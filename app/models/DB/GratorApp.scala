package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import utils.FileUtils


case class GratorApp(
    id: Option[Long] = None,
    name: String,
    path: String
){
  lazy val modules = GratorModule.findByApplication(this.id.get)

  def generateAll(): Unit = {
    val modules = GratorModule.findByApplication(this.id.get)
    for(module <- modules){
      module.generateAll
    }

    this.generateRoutes(modules)
    this.generateMessages(modules)
    this.generateMenu(modules)
  }

  def generateMessages(modules: List[GratorModule]): Unit = {
    val path = this.path+"conf/messages"

    FileUtils.writeToFile(path,views.html.gratorApp.template.messages(this.name, modules).toString)
    FileUtils.writeToFile(path+".es",views.html.gratorApp.template.messages_es(this.name, modules).toString)
  }

  def generateRoutes(modules: List[GratorModule]): Unit = {
    val path = this.path+"conf/routes"
    FileUtils.writeToFile(path,views.html.gratorApp.template.routes(modules).toString)
  }

  def generateMenu(modules: List[GratorModule]): Unit = {
    val path = this.path+"app/views/main.scala.html"
    FileUtils.writeToFile(path,views.html.gratorApp.template.main(modules).toString)
  }
}

object GratorApp{
  
  class GratorAppT(tag: Tag) extends Table[GratorApp](tag, "grator_app"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name",O.NotNull)
    def path = column[String]("path", O.NotNull)

    def * = (id.?, name, path ) <> ((GratorApp.apply _).tupled, GratorApp.unapply _)
    

  }

  val gratorAppT = TableQuery[GratorAppT]

  def save(gratorApp: GratorApp):Long = {
    DB.withTransaction { implicit session =>
      this.gratorAppT.returning(this.gratorAppT.map(_.id)).insert(gratorApp)
    }
  }
  
  def update(gratorApp: GratorApp):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- this.gratorAppT
        if s.id === gratorApp.id
      } yield(s)
      q.update(gratorApp)
    }
  }
  
  def delete(gratorApp: GratorApp):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- this.gratorAppT
        if s.id === gratorApp.id.get
      } yield(s)
      q.delete
    }
  }

  def findAllWithRelateds: List[GratorApp] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorApp <- this.gratorAppT
        
      } yield (gratorApp )
      q.list
    }
  }
  
  def findAll: List[GratorApp] = {
    DB.withSession { implicit session =>
      this.gratorAppT.list
    }
  }
  
  def findById(id: Long):Option[GratorApp] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- this.gratorAppT if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def findByIdWithRelateds(id: Long):Option[GratorApp] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorApp <- this.gratorAppT if gratorApp.id === id
        
      } yield (gratorApp )
      q.firstOption
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val gratorApps = for {
        p <- this.gratorAppT
      } yield(p)
      for(gratorApp <- gratorApps.list) yield(gratorApp.id.get.toString,gratorApp.name) 
    }
  }
}
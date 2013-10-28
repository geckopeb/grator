package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

import utils.FileUtils

case class ApplicationRow(
	id: Option[Long] = None,
	name: String,
  path: String
){
  lazy val modules = ModuleRow.findByApplication(this.id.get)

  def subProjectPath: String = {
    this.path+"grator/"
  }

  def projectPath: String = {
    this.path
  }

  def generateAll(): Unit = {
    val modules = ModuleRow.findAll
    for(module <- modules){
      module.generateAll
    }

    this.generateRoutes(modules)
    this.generateMessages(modules)
    this.generateMenu(modules)
  }

  def generateMessages(modules: List[ModuleRow]): Unit = {
    val path = this.subProjectPath+"conf/messages"

    FileUtils.writeToFile(path,views.html.application.template.messages(this.name, modules).toString)
    FileUtils.writeToFile(path+".es",views.html.application.template.messages_es(this.name, modules).toString)
  }

  def generateGratorRoutes(modules: List[ModuleRow]): Unit = {
    val path = this.subProjectPath+"grator/conf/grator.routes"
    FileUtils.writeToFile(path,views.html.application.template.module_routes(modules).toString)
  }

  def generateRootRoutes: Unit = {
    val path = this.projectPath+"conf/routes"
    FileUtils.writeIfNotExists(path,views.html.application.template.routes.toString)
  }

  def generateRoutes(modules: List[ModuleRow]): Unit = {
    this.generateGratorRoutes(modules)
    this.generateRootRoutes
  }

  def generateMenu(modules: List[ModuleRow]): Unit = {
    val path = this.subProjectPath+"app/views/main.scala.html"
    FileUtils.writeToFile(path,views.html.application.template.main(modules).toString)
  }
}

object ApplicationRow{
	def save(app: ApplicationRow):Long = {
		DB.withTransaction { implicit session =>
		  ApplicationTable.returning(ApplicationTable.id).insert(app)
		}
	}
  
  def update(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
    	//Shows.where(_.id === show.id.get).update(show)
      val q = for {
        s <- ApplicationTable
        if s.id === app.id
      } yield(s)
      q.update(app)
    }
  }
  
  def delete(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
    	//Shows.where(_.id === show.id.get).delete
    	 val q = for {
        s <- ApplicationTable
        if s.id === app.id.get
      } yield(s)
      //q.delete
      (new DeleteInvoker(q)).delete
    }
  }

	
	def findAll: List[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	Query(ApplicationTable).list
	  }
	}
	def findById(id: Long):Option[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	val q = for{
	  	  s <- ApplicationTable if s.id === id
	  	} yield (s)
	  	q.firstOption
	  }
	}

  /*
  def findByIdWithModules(id: Long): Option[(ApplicationRow,List[ModuleRow])] = {
    DB.withSession{ implicit session =>
      val q = for{
        a <- ApplicationTable if a.id === id
        m <- ModuleTable
      } yield (a,m)
      q.list
    }
  }
  */

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val applications = for {
        p <- ApplicationTable
      } yield(p)
      for(application <- applications.list) yield(application.id.get.toString,application.name) 
    }
  }
}
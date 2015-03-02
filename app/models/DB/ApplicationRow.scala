package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker

import utils.FileUtils

case class ApplicationRow(
	id: Option[Long] = None,
	name: String,
  path: String
){
  lazy val modules = ModuleRow.findByApplication(this.id.get)

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
    val path = this.path+"conf/messages"

    FileUtils.writeToFile(path,views.html.application.template.messages(this.name, modules).toString)
    FileUtils.writeToFile(path+".es",views.html.application.template.messages_es(this.name, modules).toString)
  }

  def generateRoutes(modules: List[ModuleRow]): Unit = {
    val path = this.path+"conf/routes"
    FileUtils.writeToFile(path,views.html.application.template.routes(modules).toString)
  }

  def generateMenu(modules: List[ModuleRow]): Unit = {
    val path = this.path+"app/views/main.scala.html"
    FileUtils.writeToFile(path,views.html.application.template.main(modules).toString)
  }
}

object ApplicationRow{
	def save(app: ApplicationRow):Long = {
		DB.withTransaction { implicit session =>
		  ApplicationTable.applicationTable.returning(ApplicationTable.applicationTable.map(_.id)).insert(app)
		}
	}
  
  def update(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- ApplicationTable.applicationTable
        if s.id === app.id
      } yield(s)
      q.update(app)
    }
  }
  
  def delete(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
    	 val q = for {
        s <- ApplicationTable.applicationTable
        if s.id === app.id.get
      } yield(s)
      q.delete
    }
  }

	def findAll: List[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	ApplicationTable.applicationTable.run.toList
	  }
	}
	def findById(id: Long):Option[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	val q = for{
	  	  s <- ApplicationTable.applicationTable if s.id === id
	  	} yield (s)
	  	q.firstOption
	  }
	}

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val applications = for {
        p <- ApplicationTable.applicationTable
      } yield(p)
      for(application <- applications.list) yield(application.id.get.toString,application.name) 
    }
  }
}
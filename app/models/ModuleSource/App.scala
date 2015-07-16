package it.grator.module_source

import it.grator.module_source.fields._
import it.grator.module_source.relationships._
import it.grator.module_source.subpanels.Subpanel
import it.grator.utils.FileUtils

import play.api.Logger

case class App(
	name: String,
	path: String,
	modules: List[Module],
	fields: List[Field],
	relationships: List[Relationship],
	subpanels: List[Subpanel]
){
	def getPrimaryRelationships(module: Module): List[Relationship] = {
		relationships.filter(_.primaryModule == module)
	}

	def generateRoutes(): Unit = {
		val path = this.path+"conf/routes"
		val routes = views.html.templates.app.routes(this).toString

    	FileUtils.writeToFile(path,routes)
	}

	def generateAppController(): Unit = {
		val path = this.path+"app/controllers/GratorAppController.scala"

		val controller = views.html.templates.app.grator_app_controller_template(this).toString

		FileUtils.writeToFile(path,controller)
	}

	def generateMessages(): Unit = {
		val path = this.path+"conf/messages"

	    FileUtils.writeToFile(path,       views.html.templates.app.messages.en(this, this.modules).toString)
	    FileUtils.writeToFile(path+".es", views.html.templates.app.messages.es(this, this.modules).toString)
	}

	def generateMenu(): Unit = {
	    val path = this.path+"app/views/main.scala.html"
		FileUtils.writeToFile(path,views.html.templates.app.main(this).toString)
	}

	def generateMainIndex(): Unit = {
	  val path = this.path+"app/views/index.scala.html"

	  FileUtils.writeToFile(path,views.html.templates.app.index_template().toString)
	}

	def generateApplicationController(): Unit = {
	  val path = this.path+"app/controllers/Application.scala"

	  FileUtils.writeToFile(path,views.html.templates.app.application_controller_template().toString)
	}

	def copyAssets(): Unit = {
		val destBasePath = this.path+"public/"

		FileUtils.copy("public/javascripts", destBasePath+"javascripts")
		FileUtils.copy("public/stylesheets", destBasePath+"stylesheets")
	}

	def generateHome(): Unit = {
		val path = this.path+"app/views/home/index.scala.html"

		FileUtils.writeToFile(path,views.html.templates.app.home_template().toString)
	}

	def copyBase(): Unit = {
		val destBasePath = this.path+"app/gratorBase/"

		FileUtils.copy("app/gratorBase", destBasePath)
	}

	def generateAll(): Unit = {
		this.generateRoutes()
		this.generateAppController()
	    this.generateMessages()
	    this.generateMenu()
	    this.generateMainIndex()
	    //this.generateApplicationController()
	    this.copyAssets()
	    this.generateHome()
			this.copyBase()

	    for(module <- this.modules){
	    	module.generateAll(this)
	    }
	}
}

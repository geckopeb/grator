package it.grator.module_source

import it.grator.module_source.fields._
import it.grator.module_source.relationships._
import utils.FileUtils

import play.api.Logger

case class App(
	name: String,
	path: String,
	modules: List[Module],
	fields: List[Field],
	relationships: List[Relationship]
){
	def getPrimaryRelationships(module: Module): List[Relationship] = {
		relationships.filter(_.primaryModule == module)
	}

	def generateRoutes(): Unit = {
		val path = this.path+"conf/routes"
		val routes = views.html.templates.app.routes(this).toString

    	FileUtils.writeToFile(path,routes)
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

	def generateAll(): Unit = {
		this.generateRoutes()
	    this.generateMessages()
	    this.generateMenu()

	    for(module <- this.modules){
	    	module.generateAll(this)
	    }
	}
}
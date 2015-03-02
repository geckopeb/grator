package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import language.postfixOps

class ApplicationTable(tag: Tag) extends Table[ApplicationRow](tag, "application"){
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name",O.NotNull)
  	def path = column[String]("path",O.NotNull)

	def * = (id.?, name, path) <> (ApplicationRow.apply _ tupled, ApplicationRow.unapply _)
}

object ApplicationTable{
	val applicationTable = TableQuery[ApplicationTable]
}
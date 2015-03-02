package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import language.postfixOps

class ModuleTable(tag: Tag) extends Table[ModuleRow](tag, "module"){
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name",O.NotNull)
	def applicationId = column[Long]("application_id",O.NotNull)

	def * = (id.?, name, applicationId) <> (ModuleRow.apply _ tupled, ModuleRow.unapply _)

	def application = foreignKey("module_application_id", applicationId, ApplicationTable.applicationTable)(_.id)
}

object ModuleTable{
	val moduleTable = TableQuery[ModuleTable]
}
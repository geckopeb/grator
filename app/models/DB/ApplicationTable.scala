package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object ApplicationTable extends Table[ApplicationRow]("application"){
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name",O.NotNull)
  def path = column[String]("path",O.NotNull)

	def * = id.? ~ name ~ path <> (ApplicationRow.apply _, ApplicationRow.unapply _)
}
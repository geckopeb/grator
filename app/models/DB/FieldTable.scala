package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import language.postfixOps

class FieldTable(tag: Tag) extends Table[FieldRow](tag, "field"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name",O.NotNull)
  def moduleId = column[Long]("module_id",O.NotNull)

  def fieldType = column[String]("field_type",O.NotNull)
  def required = column[Boolean]("required")

  def relatedModuleId = column[Long]("related_module_id",O.Nullable)

  def * = (id.?, name, moduleId, fieldType, required, relatedModuleId.?) <> (FieldRow.apply _ tupled, FieldRow.unapply _)

  def module = foreignKey("field_module_id", moduleId, ModuleTable.moduleTable)(_.id)
  def relatedModule = foreignKey("field_related_module_id", relatedModuleId, ModuleTable.moduleTable)(_.id)
}

object FieldTable{
	val fieldTable = TableQuery[FieldTable]
}
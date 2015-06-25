
package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row

import models.DB.GratorModule.GratorModuleT
import models.DB.GratorModule.GratorModuleT

case class GratorField(
  
    id: Option[Long] = None,
    name: String,
    moduleId: Long,
    fieldType: String,
    required: Boolean,
    relatedModuleId: Option[Long]
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorField extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  
  class GratorFieldT(tag: Tag) extends Table[GratorField](tag, "_grator_field"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def moduleId = column[Long]("module_id")
    def fieldType = column[String]("field_type")
    def required = column[Boolean]("required")
    def relatedModuleId = column[Long]("related_module_id")

    def * = ( id.?, name, moduleId, fieldType, required, relatedModuleId.? ) <> ((GratorField.apply _).tupled, GratorField.unapply _)
    def moduleIdKey = foreignKey("_grator_field__grator_module_module_id", moduleId, models.DB.GratorModule.GratorModuleT)(_.id)
	def relatedModuleIdKey = foreignKey("_grator_field__grator_module_related_module_id", relatedModuleId, models.DB.GratorModule.GratorModuleT)(_.id)
	
  }

  val GratorFieldT = TableQuery[GratorFieldT]

  def save(GratorField: GratorField): Future[Long] = {
    db.run(GratorFieldT.returning(GratorFieldT.map(_.id)) += GratorField )
  }

  def update(GratorField: GratorField): Future[Long] = {
      val q = for {
        s <- GratorFieldT
        if s.id === GratorField.id
      } yield(s)
      db.run(q.update(GratorField)).map(_.toLong)
  }

  def delete(GratorField: GratorField):Future[Int] = {
       val q = for {
        s <- GratorFieldT
        if s.id === GratorField.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorField, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        GratorField <- GratorFieldT
        moduleId <- GratorModuleT if GratorField.moduleId === moduleId.id
relatedModuleId <- GratorModuleT if GratorField.relatedModuleId === relatedModuleId.id

      } yield (GratorField , moduleId, relatedModuleId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorField]] = {
    db.run(GratorFieldT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorField]] = {
      val q = for{
        s <- GratorFieldT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorField, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        GratorField <- GratorFieldT if GratorField.id === id
        moduleId <- GratorModuleT if GratorField.moduleId === moduleId.id
relatedModuleId <- GratorModuleT if GratorField.relatedModuleId === relatedModuleId.id

      } yield (GratorField , moduleId, relatedModuleId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- GratorFieldT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorField]] = {
      val qstring = "%"+q+"%"
      val p = for{
        GratorField <- GratorFieldT if GratorField.name like qstring
      } yield (GratorField)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(GratorField: List[GratorField]) = {
    implicit val GratorFieldWrites = new Writes[GratorField] {
      def writes(GratorField: GratorField) = Json.obj(
        "value" -> GratorField.id.get,
        "label" -> GratorField.name,
        "desc" -> GratorField.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(GratorField)
    Json.stringify(jsonList)
  }
}

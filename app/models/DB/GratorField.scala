
package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row

import models.DB.GratorModule.gratorModuleT
import models.DB.GratorModule.gratorModuleT

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

  
  class GratorFieldT(tag: Tag) extends Table[GratorField](tag, "grator_field"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def moduleId = column[Long]("module_id")
    def fieldType = column[String]("field_type")
    def required = column[Boolean]("required")
    def relatedModuleId = column[Long]("related_module_id")

    def * = ( id.?, name, moduleId, fieldType, required, relatedModuleId.? ) <> ((GratorField.apply _).tupled, GratorField.unapply _)
    def moduleIdKey = foreignKey("grator_field_grator_module_module_id", moduleId, models.DB.GratorModule.gratorModuleT)(_.id)
	def relatedModuleIdKey = foreignKey("grator_field_grator_module_related_module_id", relatedModuleId, models.DB.GratorModule.gratorModuleT)(_.id)
	
  }

  val gratorFieldT = TableQuery[GratorFieldT]

  def save(gratorField: GratorField): Future[Long] = {
    db.run(gratorFieldT.returning(gratorFieldT.map(_.id)) += gratorField )
  }

  def update(gratorField: GratorField): Future[Long] = {
      val q = for {
        s <- gratorFieldT
        if s.id === gratorField.id
      } yield(s)
      db.run(q.update(gratorField)).map(_.toLong)
  }

  def delete(gratorField: GratorField):Future[Int] = {
       val q = for {
        s <- gratorFieldT
        if s.id === gratorField.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorField, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        gratorField <- gratorFieldT
        moduleId <- gratorModuleT if gratorField.moduleId === moduleId.id
relatedModuleId <- gratorModuleT if gratorField.relatedModuleId === relatedModuleId.id

      } yield (gratorField , moduleId, relatedModuleId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorField]] = {
    db.run(gratorFieldT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorField]] = {
      val q = for{
        s <- gratorFieldT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorField, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        gratorField <- gratorFieldT if gratorField.id === id
        moduleId <- gratorModuleT if gratorField.moduleId === moduleId.id
relatedModuleId <- gratorModuleT if gratorField.relatedModuleId === relatedModuleId.id

      } yield (gratorField , moduleId, relatedModuleId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorFieldT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorField]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorField <- gratorFieldT if gratorField.name like qstring
      } yield (gratorField)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorField: List[GratorField]) = {
    implicit val gratorFieldWrites = new Writes[GratorField] {
      def writes(gratorField: GratorField) = Json.obj(
        "value" -> gratorField.id.get,
        "label" -> gratorField.name,
        "desc" -> gratorField.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorField)
    Json.stringify(jsonList)
  }
}


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
import models.DB.GratorField.gratorFieldT

case class GratorSubpanel(
  
    id: Option[Long] = None,
    name: String,
    toModule: Long,
    fromModule: Long,
    fromField: Long
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorSubpanel extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  
  class GratorSubpanelT(tag: Tag) extends Table[GratorSubpanel](tag, "grator_subpanel"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def toModule = column[Long]("to_module")
    def fromModule = column[Long]("from_module")
    def fromField = column[Long]("from_field")

    def * = ( id.?, name, toModule, fromModule, fromField ) <> ((GratorSubpanel.apply _).tupled, GratorSubpanel.unapply _)
    def toModuleKey = foreignKey("grator_subpanel_grator_module_to_module", toModule, models.DB.GratorModule.gratorModuleT)(_.id)
	def fromModuleKey = foreignKey("grator_subpanel_grator_module_from_module", fromModule, models.DB.GratorModule.gratorModuleT)(_.id)
	def fromFieldKey = foreignKey("grator_subpanel_grator_field_from_field", fromField, models.DB.GratorField.gratorFieldT)(_.id)
	
  }

  val gratorSubpanelT = TableQuery[GratorSubpanelT]

  def save(gratorSubpanel: GratorSubpanel): Future[Long] = {
    db.run(gratorSubpanelT.returning(gratorSubpanelT.map(_.id)) += gratorSubpanel )
  }

  def update(gratorSubpanel: GratorSubpanel): Future[Long] = {
      val q = for {
        s <- gratorSubpanelT
        if s.id === gratorSubpanel.id
      } yield(s)
      db.run(q.update(gratorSubpanel)).map(_.toLong)
  }

  def delete(gratorSubpanel: GratorSubpanel):Future[Int] = {
       val q = for {
        s <- gratorSubpanelT
        if s.id === gratorSubpanel.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorSubpanel, models.DB.GratorModule, models.DB.GratorModule, models.DB.GratorField )]] = {
      val q = for {
        gratorSubpanel <- gratorSubpanelT
        toModule <- gratorModuleT if gratorSubpanel.toModule === toModule.id
fromModule <- gratorModuleT if gratorSubpanel.fromModule === fromModule.id
fromField <- gratorFieldT if gratorSubpanel.fromField === fromField.id

      } yield (gratorSubpanel , toModule, fromModule, fromField)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorSubpanel]] = {
    db.run(gratorSubpanelT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorSubpanel]] = {
      val q = for{
        s <- gratorSubpanelT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorSubpanel, models.DB.GratorModule, models.DB.GratorModule, models.DB.GratorField )]] = {
      val q = for {
        gratorSubpanel <- gratorSubpanelT if gratorSubpanel.id === id
        toModule <- gratorModuleT if gratorSubpanel.toModule === toModule.id
fromModule <- gratorModuleT if gratorSubpanel.fromModule === fromModule.id
fromField <- gratorFieldT if gratorSubpanel.fromField === fromField.id

      } yield (gratorSubpanel , toModule, fromModule, fromField)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorSubpanelT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorSubpanel]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorSubpanel <- gratorSubpanelT if gratorSubpanel.name like qstring
      } yield (gratorSubpanel)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorSubpanel: List[GratorSubpanel]) = {
    implicit val gratorSubpanelWrites = new Writes[GratorSubpanel] {
      def writes(gratorSubpanel: GratorSubpanel) = Json.obj(
        "value" -> gratorSubpanel.id.get,
        "label" -> gratorSubpanel.name,
        "desc" -> gratorSubpanel.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorSubpanel)
    Json.stringify(jsonList)
  }

  

  def findByToModuleWithRelateds(id: Long): Future[List[( models.DB.GratorSubpanel, models.DB.GratorModule, models.DB.GratorModule, models.DB.GratorField )]] = {
      val q = for {
        gratorSubpanel <- gratorSubpanelT if gratorSubpanel.toModule === id
        toModule <- gratorModuleT if gratorSubpanel.toModule === toModule.id
fromModule <- gratorModuleT if gratorSubpanel.fromModule === fromModule.id
fromField <- gratorFieldT if gratorSubpanel.fromField === fromField.id

      } yield (gratorSubpanel , toModule, fromModule, fromField)
      db.run(q.result).map(_.toList)
  }

  def findByFromModuleWithRelateds(id: Long): Future[List[( models.DB.GratorSubpanel, models.DB.GratorModule, models.DB.GratorModule, models.DB.GratorField )]] = {
      val q = for {
        gratorSubpanel <- gratorSubpanelT if gratorSubpanel.fromModule === id
        toModule <- gratorModuleT if gratorSubpanel.toModule === toModule.id
fromModule <- gratorModuleT if gratorSubpanel.fromModule === fromModule.id
fromField <- gratorFieldT if gratorSubpanel.fromField === fromField.id

      } yield (gratorSubpanel , toModule, fromModule, fromField)
      db.run(q.result).map(_.toList)
  }

  def findByFromFieldWithRelateds(id: Long): Future[List[( models.DB.GratorSubpanel, models.DB.GratorModule, models.DB.GratorModule, models.DB.GratorField )]] = {
      val q = for {
        gratorSubpanel <- gratorSubpanelT if gratorSubpanel.fromField === id
        toModule <- gratorModuleT if gratorSubpanel.toModule === toModule.id
fromModule <- gratorModuleT if gratorSubpanel.fromModule === fromModule.id
fromField <- gratorFieldT if gratorSubpanel.fromField === fromField.id

      } yield (gratorSubpanel , toModule, fromModule, fromField)
      db.run(q.result).map(_.toList)
  }



  /* CUSTOM CODE */
  def findAllByApplicationId(applicationId: Long): Future[List[GratorSubpanel]] = {
    val q = for{
      mod <- GratorModule.gratorModuleT if mod.applicationId === applicationId
      sub <- GratorSubpanel.gratorSubpanelT if mod.id === sub.fromModule
    } yield (sub)
    db.run(q.result).map(_.toList)
  }
  /* END CUSTOM CODE */
}

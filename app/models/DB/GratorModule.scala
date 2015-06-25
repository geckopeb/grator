
package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row

import models.DB.GratorApp.GratorAppT

case class GratorModule(
  
    id: Option[Long] = None,
    name: String,
    applicationId: Long
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorModule extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  
  class GratorModuleT(tag: Tag) extends Table[GratorModule](tag, "_grator_module"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def applicationId = column[Long]("application_id")

    def * = ( id.?, name, applicationId ) <> ((GratorModule.apply _).tupled, GratorModule.unapply _)
    def applicationIdKey = foreignKey("_grator_module__grator_app_application_id", applicationId, models.DB.GratorApp.GratorAppT)(_.id)
	
  }

  val GratorModuleT = TableQuery[GratorModuleT]

  def save(GratorModule: GratorModule): Future[Long] = {
    db.run(GratorModuleT.returning(GratorModuleT.map(_.id)) += GratorModule )
  }

  def update(GratorModule: GratorModule): Future[Long] = {
      val q = for {
        s <- GratorModuleT
        if s.id === GratorModule.id
      } yield(s)
      db.run(q.update(GratorModule)).map(_.toLong)
  }

  def delete(GratorModule: GratorModule):Future[Int] = {
       val q = for {
        s <- GratorModuleT
        if s.id === GratorModule.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorModule, models.DB.GratorApp )]] = {
      val q = for {
        GratorModule <- GratorModuleT
        applicationId <- GratorAppT if GratorModule.applicationId === applicationId.id

      } yield (GratorModule , applicationId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorModule]] = {
    db.run(GratorModuleT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorModule]] = {
      val q = for{
        s <- GratorModuleT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorModule, models.DB.GratorApp )]] = {
      val q = for {
        GratorModule <- GratorModuleT if GratorModule.id === id
        applicationId <- GratorAppT if GratorModule.applicationId === applicationId.id

      } yield (GratorModule , applicationId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- GratorModuleT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorModule]] = {
      val qstring = "%"+q+"%"
      val p = for{
        GratorModule <- GratorModuleT if GratorModule.name like qstring
      } yield (GratorModule)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(GratorModule: List[GratorModule]) = {
    implicit val GratorModuleWrites = new Writes[GratorModule] {
      def writes(GratorModule: GratorModule) = Json.obj(
        "value" -> GratorModule.id.get,
        "label" -> GratorModule.name,
        "desc" -> GratorModule.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(GratorModule)
    Json.stringify(jsonList)
  }
}

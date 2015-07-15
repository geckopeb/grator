
package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row

import models.DB.GratorApp.gratorAppT
import models.DB.GratorField
import models.DB.GratorField.gratorFieldT

case class GratorModule(

    id: Option[Long] = None,
    name: String,
    applicationId: Long,
    hasTab: Boolean
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorModule extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)


  class GratorModuleT(tag: Tag) extends Table[GratorModule](tag, "grator_module"){

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def applicationId = column[Long]("application_id")
    def hasTab = column[Boolean]("has_tab")

    def * = ( id.?, name, applicationId, hasTab ) <> ((GratorModule.apply _).tupled, GratorModule.unapply _)
    def applicationIdKey = foreignKey("grator_module_grator_app_application_id", applicationId, models.DB.GratorApp.gratorAppT)(_.id)

  }

  val gratorModuleT = TableQuery[GratorModuleT]

  def save(gratorModule: GratorModule): Future[Long] = {
    db.run(
      (
        for {
          gmid <- gratorModuleT.returning(gratorModuleT.map(_.id)) += gratorModule
          _ <- gratorFieldT ++= Seq(
                                  GratorField(None, "id", gmid, 1, true, Some(gmid)),
                                  GratorField(None, "name", gmid, 2, true, Some(gmid))
                                )
        } yield gmid).transactionally
    )
  }

  def update(gratorModule: GratorModule): Future[Long] = {
      val q = for {
        s <- gratorModuleT
        if s.id === gratorModule.id
      } yield(s)
      db.run(q.update(gratorModule)).map(_.toLong)
  }

  def delete(gratorModule: GratorModule):Future[Int] = {
       val q = for {
        s <- gratorModuleT
        if s.id === gratorModule.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorModule, models.DB.GratorApp )]] = {
      val q = for {
        gratorModule <- gratorModuleT
        applicationId <- gratorAppT if gratorModule.applicationId === applicationId.id

      } yield (gratorModule , applicationId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorModule]] = {
    db.run(gratorModuleT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorModule]] = {
      val q = for{
        s <- gratorModuleT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorModule, models.DB.GratorApp )]] = {
      val q = for {
        gratorModule <- gratorModuleT if gratorModule.id === id
        applicationId <- gratorAppT if gratorModule.applicationId === applicationId.id

      } yield (gratorModule , applicationId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorModuleT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorModule]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorModule <- gratorModuleT if gratorModule.name like qstring
      } yield (gratorModule)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorModule: List[GratorModule]) = {
    implicit val gratorModuleWrites = new Writes[GratorModule] {
      def writes(gratorModule: GratorModule) = Json.obj(
        "value" -> gratorModule.id.get,
        "label" -> gratorModule.name,
        "desc" -> gratorModule.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorModule)
    Json.stringify(jsonList)
  }

  /* CUSTOM CODE */
  def findAllByApplicationId(appId: Long): Future[List[GratorModule]] = {
    val q = gratorModuleT.filter(_.applicationId === appId)
    db.run(q.result).map(_.toList)
  }
  /* END CUSTOM CODE */
}


package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row

case class GratorApp(
    id: Option[Long] = None,
    name: String,
    path: String
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorApp extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)


  class GratorAppT(tag: Tag) extends Table[GratorApp](tag, "grator_app"){

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def path = column[String]("path")

    def * = ( id.?, name, path ) <> ((GratorApp.apply _).tupled, GratorApp.unapply _)

  }

  val gratorAppT = TableQuery[GratorAppT]

  def save(gratorApp: GratorApp): Future[Long] = {
    db.run(gratorAppT.returning(gratorAppT.map(_.id)) += gratorApp )
  }

  def update(gratorApp: GratorApp): Future[Long] = {
      val q = for {
        s <- gratorAppT
        if s.id === gratorApp.id
      } yield(s)
      db.run(q.update(gratorApp)).map(_.toLong)
  }

  def delete(gratorApp: GratorApp):Future[Int] = {
       val q = for {
        s <- gratorAppT
        if s.id === gratorApp.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[models.DB.GratorApp]] = {
      val q = for {
        gratorApp <- gratorAppT

      } yield (gratorApp )
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorApp]] = {
    db.run(gratorAppT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorApp]] = {
      val q = for{
        s <- gratorAppT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[models.DB.GratorApp]] = {
      val q = for {
        gratorApp <- gratorAppT if gratorApp.id === id

      } yield (gratorApp )
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorAppT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorApp]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorApp <- gratorAppT if gratorApp.name like qstring
      } yield (gratorApp)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorApp: List[GratorApp]) = {
    implicit val gratorAppWrites = new Writes[GratorApp] {
      def writes(gratorApp: GratorApp) = Json.obj(
        "value" -> gratorApp.id.get,
        "label" -> gratorApp.name,
        "desc" -> gratorApp.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorApp)
    Json.stringify(jsonList)
  }
}


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

  
  class GratorAppT(tag: Tag) extends Table[GratorApp](tag, "_grator_app"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def path = column[String]("path")

    def * = ( id.?, name, path ) <> ((GratorApp.apply _).tupled, GratorApp.unapply _)
    
  }

  val GratorAppT = TableQuery[GratorAppT]

  def save(GratorApp: GratorApp): Future[Long] = {
    db.run(GratorAppT.returning(GratorAppT.map(_.id)) += GratorApp )
  }

  def update(GratorApp: GratorApp): Future[Long] = {
      val q = for {
        s <- GratorAppT
        if s.id === GratorApp.id
      } yield(s)
      db.run(q.update(GratorApp)).map(_.toLong)
  }

  def delete(GratorApp: GratorApp):Future[Int] = {
       val q = for {
        s <- GratorAppT
        if s.id === GratorApp.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[models.DB.GratorApp]] = {
      val q = for {
        GratorApp <- GratorAppT
        
      } yield (GratorApp )
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorApp]] = {
    db.run(GratorAppT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorApp]] = {
      val q = for{
        s <- GratorAppT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[models.DB.GratorApp]] = {
      val q = for {
        GratorApp <- GratorAppT if GratorApp.id === id
        
      } yield (GratorApp )
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- GratorAppT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorApp]] = {
      val qstring = "%"+q+"%"
      val p = for{
        GratorApp <- GratorAppT if GratorApp.name like qstring
      } yield (GratorApp)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(GratorApp: List[GratorApp]) = {
    implicit val GratorAppWrites = new Writes[GratorApp] {
      def writes(GratorApp: GratorApp) = Json.obj(
        "value" -> GratorApp.id.get,
        "label" -> GratorApp.name,
        "desc" -> GratorApp.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(GratorApp)
    Json.stringify(jsonList)
  }
}

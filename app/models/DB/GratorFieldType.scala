
package models.DB

import scala.concurrent.Future

import play.api.libs.json._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.driver.JdbcProfile

import it.grator.grator_base.Row


case class GratorFieldType(
  
    id: Option[Long] = None,
    name: String
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorFieldType extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  
  class GratorFieldTypeT(tag: Tag) extends Table[GratorFieldType](tag, "grator_field_type"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = ( id.?, name ) <> ((GratorFieldType.apply _).tupled, GratorFieldType.unapply _)
    
  }

  val gratorFieldTypeT = TableQuery[GratorFieldTypeT]

  def save(gratorFieldType: GratorFieldType): Future[Long] = {
    db.run(gratorFieldTypeT.returning(gratorFieldTypeT.map(_.id)) += gratorFieldType )
  }

  def update(gratorFieldType: GratorFieldType): Future[Long] = {
      val q = for {
        s <- gratorFieldTypeT
        if s.id === gratorFieldType.id
      } yield(s)
      db.run(q.update(gratorFieldType)).map(_.toLong)
  }

  def delete(gratorFieldType: GratorFieldType):Future[Int] = {
       val q = for {
        s <- gratorFieldTypeT
        if s.id === gratorFieldType.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[models.DB.GratorFieldType]] = {
      val q = for {
        gratorFieldType <- gratorFieldTypeT
        
      } yield (gratorFieldType )
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorFieldType]] = {
    db.run(gratorFieldTypeT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorFieldType]] = {
      val q = for{
        s <- gratorFieldTypeT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[models.DB.GratorFieldType]] = {
      val q = for {
        gratorFieldType <- gratorFieldTypeT if gratorFieldType.id === id
        
      } yield (gratorFieldType )
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorFieldTypeT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorFieldType]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorFieldType <- gratorFieldTypeT if gratorFieldType.name like qstring
      } yield (gratorFieldType)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorFieldType: List[GratorFieldType]) = {
    implicit val gratorFieldTypeWrites = new Writes[GratorFieldType] {
      def writes(gratorFieldType: GratorFieldType) = Json.obj(
        "value" -> gratorFieldType.id.get,
        "label" -> gratorFieldType.name,
        "desc" -> gratorFieldType.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorFieldType)
    Json.stringify(jsonList)
  }

  



  /* CUSTOM CODE */
  /* END CUSTOM CODE */
}

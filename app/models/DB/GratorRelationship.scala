
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

case class GratorRelationship(
  
    id: Option[Long] = None,
    name: String,
    relType: String,
    primaryModuleId: Long,
    primaryModuleLabel: String,
    primaryModuleSubpanel: String,
    relatedModuleId: Long,
    relatedModuleLabel: String,
    relatedModuleSubpanel: String
) extends Row{
  def description: String = this.id+"-"+this.name
}

object GratorRelationship extends HasDatabaseConfig[JdbcProfile]{
  import driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  
  class GratorRelationshipT(tag: Tag) extends Table[GratorRelationship](tag, "_grator_relationship"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def relType = column[String]("rel_type")
    def primaryModuleId = column[Long]("primary_module_id")
    def primaryModuleLabel = column[String]("primary_module_label")
    def primaryModuleSubpanel = column[String]("primary_module_subpanel")
    def relatedModuleId = column[Long]("related_module_id")
    def relatedModuleLabel = column[String]("related_module_label")
    def relatedModuleSubpanel = column[String]("related_module_subpanel")

    def * = ( id.?, name, relType, primaryModuleId, primaryModuleLabel, primaryModuleSubpanel, relatedModuleId, relatedModuleLabel, relatedModuleSubpanel ) <> ((GratorRelationship.apply _).tupled, GratorRelationship.unapply _)
    def primaryModuleIdKey = foreignKey("_grator_relationship__grator_module_primary_module_id", primaryModuleId, models.DB.GratorModule.GratorModuleT)(_.id)
	def relatedModuleIdKey = foreignKey("_grator_relationship__grator_module_related_module_id", relatedModuleId, models.DB.GratorModule.GratorModuleT)(_.id)
	
  }

  val GratorRelationshipT = TableQuery[GratorRelationshipT]

  def save(GratorRelationship: GratorRelationship): Future[Long] = {
    db.run(GratorRelationshipT.returning(GratorRelationshipT.map(_.id)) += GratorRelationship )
  }

  def update(GratorRelationship: GratorRelationship): Future[Long] = {
      val q = for {
        s <- GratorRelationshipT
        if s.id === GratorRelationship.id
      } yield(s)
      db.run(q.update(GratorRelationship)).map(_.toLong)
  }

  def delete(GratorRelationship: GratorRelationship):Future[Int] = {
       val q = for {
        s <- GratorRelationshipT
        if s.id === GratorRelationship.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorRelationship, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        GratorRelationship <- GratorRelationshipT
        primaryModuleId <- GratorModuleT if GratorRelationship.primaryModuleId === primaryModuleId.id
relatedModuleId <- GratorModuleT if GratorRelationship.relatedModuleId === relatedModuleId.id

      } yield (GratorRelationship , primaryModuleId, relatedModuleId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorRelationship]] = {
    db.run(GratorRelationshipT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorRelationship]] = {
      val q = for{
        s <- GratorRelationshipT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorRelationship, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        GratorRelationship <- GratorRelationshipT if GratorRelationship.id === id
        primaryModuleId <- GratorModuleT if GratorRelationship.primaryModuleId === primaryModuleId.id
relatedModuleId <- GratorModuleT if GratorRelationship.relatedModuleId === relatedModuleId.id

      } yield (GratorRelationship , primaryModuleId, relatedModuleId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- GratorRelationshipT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorRelationship]] = {
      val qstring = "%"+q+"%"
      val p = for{
        GratorRelationship <- GratorRelationshipT if GratorRelationship.name like qstring
      } yield (GratorRelationship)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(GratorRelationship: List[GratorRelationship]) = {
    implicit val GratorRelationshipWrites = new Writes[GratorRelationship] {
      def writes(GratorRelationship: GratorRelationship) = Json.obj(
        "value" -> GratorRelationship.id.get,
        "label" -> GratorRelationship.name,
        "desc" -> GratorRelationship.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(GratorRelationship)
    Json.stringify(jsonList)
  }
}


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


  class GratorRelationshipT(tag: Tag) extends Table[GratorRelationship](tag, "grator_relationship"){

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
    def primaryModuleIdKey = foreignKey("grator_relationship_grator_module_primary_module_id", primaryModuleId, models.DB.GratorModule.gratorModuleT)(_.id)
	def relatedModuleIdKey = foreignKey("grator_relationship_grator_module_related_module_id", relatedModuleId, models.DB.GratorModule.gratorModuleT)(_.id)

  }

  val gratorRelationshipT = TableQuery[GratorRelationshipT]

  def save(gratorRelationship: GratorRelationship): Future[Long] = {
    db.run(gratorRelationshipT.returning(gratorRelationshipT.map(_.id)) += gratorRelationship )
  }

  def update(gratorRelationship: GratorRelationship): Future[Long] = {
      val q = for {
        s <- gratorRelationshipT
        if s.id === gratorRelationship.id
      } yield(s)
      db.run(q.update(gratorRelationship)).map(_.toLong)
  }

  def delete(gratorRelationship: GratorRelationship):Future[Int] = {
       val q = for {
        s <- gratorRelationshipT
        if s.id === gratorRelationship.id.get
      } yield(s)
      db.run(q.delete)
  }

  def findAllWithRelateds: Future[List[( models.DB.GratorRelationship, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        gratorRelationship <- gratorRelationshipT
        primaryModuleId <- gratorModuleT if gratorRelationship.primaryModuleId === primaryModuleId.id
relatedModuleId <- gratorModuleT if gratorRelationship.relatedModuleId === relatedModuleId.id

      } yield (gratorRelationship , primaryModuleId, relatedModuleId)
      db.run(q.result).map(_.toList)
  }

  def findAll: Future[List[GratorRelationship]] = {
    db.run(gratorRelationshipT.result).map(_.toList)
  }

  def findById(id: Long): Future[Option[GratorRelationship]] = {
      val q = for{
        s <- gratorRelationshipT if s.id === id
      } yield (s)
      db.run(q.result).map(_.headOption)
  }

  def findByIdWithRelateds(id: Long): Future[Option[( models.DB.GratorRelationship, models.DB.GratorModule, models.DB.GratorModule )]] = {
      val q = for {
        gratorRelationship <- gratorRelationshipT if gratorRelationship.id === id
        primaryModuleId <- gratorModuleT if gratorRelationship.primaryModuleId === primaryModuleId.id
relatedModuleId <- gratorModuleT if gratorRelationship.relatedModuleId === relatedModuleId.id

      } yield (gratorRelationship , primaryModuleId, relatedModuleId)
      db.run(q.result).map(_.headOption)
  }

  def getOptions(): Future[Seq[(String,String)]] = {
    val q = for {
      p <- gratorRelationshipT
    } yield(p.id, p.name)

    db.run(q.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByQueryString(q: String): Future[List[GratorRelationship]] = {
      val qstring = "%"+q+"%"
      val p = for{
        gratorRelationship <- gratorRelationshipT if gratorRelationship.name like qstring
      } yield (gratorRelationship)
      db.run(p.result).map(_.toList)
  }

  def toJsonRelatedCombo(gratorRelationship: List[GratorRelationship]) = {
    implicit val gratorRelationshipWrites = new Writes[GratorRelationship] {
      def writes(gratorRelationship: GratorRelationship) = Json.obj(
        "value" -> gratorRelationship.id.get,
        "label" -> gratorRelationship.name,
        "desc" -> gratorRelationship.toString //ESP pendiente generar una descripción!!!
      )
    }
    val jsonList = Json.toJson(gratorRelationship)
    Json.stringify(jsonList)
  }

  /* CUSTOM CODE */
  def findAllByApplicationId(applicationId: Long): Future[List[GratorRelationship]] = {
    val q = for{
      s <- GratorModule.gratorModuleT if s.applicationId === applicationId
      r <- GratorRelationship.gratorRelationshipT if s.id === r.primaryModuleId
    } yield (r)
    db.run(q.result).map(_.toList)
  }
  /* END CUSTOM CODE */
}


package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import GratorModule.gratorModuleT

import models.Module

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
){
  def primaryModule = GratorModule.findById(this.primaryModuleId)
  def primaryModuleModule: Option[Module] = this.primaryModule match {
    case Some(m: GratorModule) => Some(new Module(m))
    case None => None
  }
  def relatedModule = GratorModule.findById(this.relatedModuleId)
  def relatedModuleModule: Option[Module] = this.relatedModule match {
    case Some(m: GratorModule) => Some(new Module(m))
    case None => None
  }
  
}

object GratorRelationship{
  
  class GratorRelationshipT(tag: Tag) extends Table[GratorRelationship](tag, "grator_relationship"){
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name",O.NotNull)
    def relType = column[String]("rel_type", O.NotNull)
    def primaryModuleId = column[Long]("primary_module_id", O.NotNull)
    def primaryModuleLabel = column[String]("primary_module_label", O.NotNull)
    def primaryModuleSubpanel = column[String]("primary_module_subpanel", O.NotNull)
    def relatedModuleId = column[Long]("related_module_id", O.NotNull)
    def relatedModuleLabel = column[String]("related_module_label", O.NotNull)
    def relatedModuleSubpanel = column[String]("related_module_subpanel", O.NotNull)

    def * = (id.?, name, relType, primaryModuleId, primaryModuleLabel, primaryModuleSubpanel, relatedModuleId, relatedModuleLabel, relatedModuleSubpanel ) <> ((GratorRelationship.apply _).tupled, GratorRelationship.unapply _)
    
    def primaryModuleIdId = foreignKey("grator_relationship_grator_module_primary_module_id", primaryModuleId, GratorModule.gratorModuleT)(_.id)
    def relatedModuleIdId = foreignKey("grator_relationship_grator_module_related_module_id", relatedModuleId, GratorModule.gratorModuleT)(_.id)

  }

  val gratorRelationshipT = TableQuery[GratorRelationshipT]

  def save(gratorRelationship: GratorRelationship):Long = {
    DB.withTransaction { implicit session =>
      this.gratorRelationshipT.returning(this.gratorRelationshipT.map(_.id)).insert(gratorRelationship)
    }
  }
  
  def update(gratorRelationship: GratorRelationship):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- this.gratorRelationshipT
        if s.id === gratorRelationship.id
      } yield(s)
      q.update(gratorRelationship)
    }
  }
  
  def delete(gratorRelationship: GratorRelationship):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- this.gratorRelationshipT
        if s.id === gratorRelationship.id.get
      } yield(s)
      q.delete
    }
  }

  def findAllWithRelateds: List[( GratorRelationship , GratorModule, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorRelationship <- this.gratorRelationshipT
        gratorModuleprimaryModuleId <- gratorModuleT if gratorRelationship.primaryModuleId === gratorModuleprimaryModuleId.id
gratorModulerelatedModuleId <- gratorModuleT if gratorRelationship.relatedModuleId === gratorModulerelatedModuleId.id

      } yield (gratorRelationship , gratorModuleprimaryModuleId, gratorModulerelatedModuleId)
      q.list
    }
  }

  //                                                      Relationship,       RelatedModule
  def findByPrimaryModuleId(primaryModuleId: Long): List[(GratorRelationship, GratorModule)] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorRelationship <- this.gratorRelationshipT if gratorRelationship.primaryModuleId === primaryModuleId
        gratorModulerelatedModuleId <- gratorModuleT if gratorRelationship.relatedModuleId === gratorModulerelatedModuleId.id
      } yield (gratorRelationship, gratorModulerelatedModuleId)
      q.list
    }
  }

  //                                                       Relationship,       PrimaryModule
  def findByRelatedModuleId(relatedModuleId: Long): List[( GratorRelationship, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorRelationship <- this.gratorRelationshipT if gratorRelationship.relatedModuleId === relatedModuleId
        gratorModuleprimaryModuleId <- gratorModuleT if gratorRelationship.primaryModuleId === gratorModuleprimaryModuleId.id
      } yield (gratorRelationship , gratorModuleprimaryModuleId)
      q.list
    }
  }
  
  def findAll: List[GratorRelationship] = {
    DB.withSession { implicit session =>
      this.gratorRelationshipT.list
    }
  }
  
  def findById(id: Long):Option[GratorRelationship] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- this.gratorRelationshipT if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def findByIdWithRelateds(id: Long):Option[( GratorRelationship , GratorModule, GratorModule )] = {
    DB.withSession { implicit session =>
      val q = for {
        gratorRelationship <- this.gratorRelationshipT if gratorRelationship.id === id
        gratorModuleprimaryModuleId <- gratorModuleT if gratorRelationship.primaryModuleId === gratorModuleprimaryModuleId.id
gratorModulerelatedModuleId <- gratorModuleT if gratorRelationship.relatedModuleId === gratorModulerelatedModuleId.id

      } yield (gratorRelationship , gratorModuleprimaryModuleId, gratorModulerelatedModuleId)
      q.firstOption
    }
  }

  def findByApplication(applicationId: Long): List[GratorRelationship] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- GratorModule.gratorModuleT if s.applicationId === applicationId
        r <- GratorRelationship.gratorRelationshipT if s.id === r.primaryModuleId
      } yield (r)
      q.list
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val gratorRelationships = for {
        p <- this.gratorRelationshipT
      } yield(p)
      for(gratorRelationship <- gratorRelationships.list) yield(gratorRelationship.id.get.toString,gratorRelationship.name) 
    }
  }

  def getRelTypeOptions(): Seq[(String,String)] = {
    Seq(
      //("1aN","1aN"),
      ("NaN","NaN")
    )
  }

  def getSubpanelOptions(): Seq[(String,String)] = {
    Seq(
      ("yes","yes")
      //("no","no")
    )
  }
}
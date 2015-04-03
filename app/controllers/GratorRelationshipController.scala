
package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

object GratorRelationshipController extends Controller {
  def index = Action {
    val gratorRelationships = GratorRelationship.findAllWithRelateds

    Ok(views.html.gratorRelationship.index(gratorRelationships))
  }

  val GratorRelationshipForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"relType" -> nonEmptyText,
					"primaryModuleId" -> longNumber,
					"primaryModuleLabel" -> nonEmptyText,
					"primaryModuleSubpanel" -> nonEmptyText,
					"relatedModuleId" -> longNumber,
					"relatedModuleLabel" -> nonEmptyText,
					"relatedModuleSubpanel" -> nonEmptyText

      )(GratorRelationship.apply)(GratorRelationship.unapply)
  )

  def insert = Action {
    Ok(views.html.gratorRelationship.insert(GratorRelationshipForm, GratorRelationship.getRelTypeOptions, GratorRelationship.getSubpanelOptions))
  }
  

  def detail(id: Long) = Action {
    GratorRelationship.findByIdWithRelateds(id).map{
      gratorRelationship => Ok(views.html.gratorRelationship.detail(gratorRelationship))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    GratorRelationshipForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.gratorRelationship.insert(formWithErrors, GratorRelationship.getRelTypeOptions, GratorRelationship.getSubpanelOptions)),
      gratorRelationship => {
        val id = GratorRelationship.save(gratorRelationship)
        Redirect(routes.GratorRelationshipController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    GratorRelationship.findById(id).map{
      gratorRelationship:GratorRelationship => Ok(views.html.gratorRelationship.edit(GratorRelationshipForm.fill(gratorRelationship),gratorRelationship, GratorRelationship.getRelTypeOptions, GratorRelationship.getSubpanelOptions))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    GratorRelationship.findById(id).map{
      gratorRelationship => {
          GratorRelationship.delete(gratorRelationship)
          Redirect(routes.GratorRelationshipController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    GratorRelationship.findById(id).map{
      gratorRelationship => {
      GratorRelationshipForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.gratorRelationship.edit(formWithErrors,gratorRelationship, GratorRelationship.getRelTypeOptions, GratorRelationship.getSubpanelOptions)),
        gratorRelationship => {
          GratorRelationship.update(gratorRelationship)
          Redirect(routes.GratorRelationshipController.detail(gratorRelationship.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }
}
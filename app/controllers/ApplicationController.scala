package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

object ApplicationController extends Controller {
  def index = Action {
    //Ok(views.html.index("Your new application is ready."))
    val applications = ApplicationRow.findAll

    Ok(views.html.application.index(applications))
  }

  val applicationForm = Form(
	    mapping(
	      "id" -> optional(longNumber),
	      "name" -> nonEmptyText,
        "path" -> nonEmptyText
	    )(ApplicationRow.apply)(ApplicationRow.unapply)
	)

  def insert = Action {
	  Ok(views.html.application.insert(applicationForm))
  }
	

  def detail(id: Long) = Action {
    ApplicationRow.findById(id).map{
      application => {
        //val modules = ModuleRow.findByApplicationId(application.id.get)
        Ok(views.html.application.detail(application,application.modules))
      }
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    applicationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.application.insert(formWithErrors)),
      application => {
        val id = ApplicationRow.save(application)
        Redirect(routes.ApplicationController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    ApplicationRow.findById(id).map{
      application:ApplicationRow => Ok(views.html.application.edit(applicationForm.fill(application),application))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    ApplicationRow.findById(id).map{
      applicationRow => {
          ApplicationRow.delete(applicationRow)
          Redirect(routes.ApplicationController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    ApplicationRow.findById(id).map{
      application => {
      applicationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.application.edit(formWithErrors,application)),
        application => {
          ApplicationRow.update(application)
          Redirect(routes.ApplicationController.detail(application.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    ApplicationRow.findById(id).map{
      application:ApplicationRow => {
        application.generateAll
        Redirect(routes.ApplicationController.detail(application.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}
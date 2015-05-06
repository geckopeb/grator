package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import play.api.Logger

import it.grator.module_source.{App, AppFactory}

object GratorAppController extends Controller {
  def index = Action {
    val gratorApps = GratorApp.findAllWithRelateds

    Ok(views.html.gratorApp.index(gratorApps))
  }

  val GratorAppForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"path" -> nonEmptyText

      )(GratorApp.apply)(GratorApp.unapply)
  )

  def insert = Action {
    Ok(views.html.gratorApp.insert(GratorAppForm))
  }
  

  def detail(id: Long) = Action {
    GratorApp.findByIdWithRelateds(id).map{
      gratorApp => Ok(views.html.gratorApp.detail(gratorApp, GratorModule.findByApplicationWithRelateds(gratorApp.id.get)))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    GratorAppForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.gratorApp.insert(formWithErrors)),
      gratorApp => {
        val id = GratorApp.save(gratorApp)
        Redirect(routes.GratorAppController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    GratorApp.findById(id).map{
      gratorApp:GratorApp => Ok(views.html.gratorApp.edit(GratorAppForm.fill(gratorApp),gratorApp))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    GratorApp.findById(id).map{
      gratorApp => {
          GratorApp.delete(gratorApp)
          Redirect(routes.GratorAppController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    GratorApp.findById(id).map{
      gratorApp => {
      GratorAppForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.gratorApp.edit(formWithErrors,gratorApp)),
        gratorApp => {
          GratorApp.update(gratorApp)
          Redirect(routes.GratorAppController.detail(gratorApp.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    GratorApp.findById(id).map{
      application:GratorApp => {
        val gModules = application.modules
        val gFields = application.fields
        val gRelationships = application.relationships

        //val app = AppFactory.construct(application.name, application.path, gModules, gFields, gRelationships)
        val app = AppFactory.construct(application.name, application.path, gModules, gFields, gRelationships)
        app.generateAll()

        Redirect(routes.GratorAppController.detail(application.id.get)) 
      }
    }.getOrElse(NotFound)
  }


  def backupAll(id: Long) = Action {
    GratorApp.findById(id).map{
      application:GratorApp => {
        application.backupAll
        Redirect(routes.GratorAppController.detail(application.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}
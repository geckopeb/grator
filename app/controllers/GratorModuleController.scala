package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

object GratorModuleController extends Controller {
  def index = Action {
    val gratorModules = GratorModule.findAllWithRelateds

    Ok(views.html.gratorModule.index(gratorModules))
  }

  val GratorModuleForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"applicationId" -> longNumber

      )(GratorModule.apply)(GratorModule.unapply)
  )

  def insert = Action {
    Ok(views.html.gratorModule.insert(GratorModuleForm, GratorApp.getOptions))
  }
  

  def detail(id: Long) = Action {
    GratorModule.findByIdWithRelateds(id).map{
      gratorModule => Ok(views.html.gratorModule.detail(gratorModule, GratorField.findByModuleIdWithRelateds(gratorModule._1.id.get)))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    GratorModuleForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.gratorModule.insert(formWithErrors, GratorApp.getOptions)),
      gratorModule => {
        val id = GratorModule.save(gratorModule)
        Redirect(routes.GratorModuleController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    GratorModule.findById(id).map{
      gratorModule:GratorModule => Ok(views.html.gratorModule.edit(GratorModuleForm.fill(gratorModule), GratorApp.getOptions,gratorModule))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    GratorModule.findById(id).map{
      gratorModule => {
          GratorModule.delete(gratorModule)
          Redirect(routes.GratorModuleController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    GratorModule.findById(id).map{
      gratorModule => {
      GratorModuleForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.gratorModule.edit(formWithErrors, GratorApp.getOptions,gratorModule)),
        gratorModule => {
          GratorModule.update(gratorModule)
          Redirect(routes.GratorModuleController.detail(gratorModule.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    GratorModule.findById(id).map{
      module:GratorModule => {
        module.generateAll
        Redirect(routes.GratorModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateController(id: Long) = Action {
    GratorModule.findById(id).map{
      module:GratorModule => {
        module.generateController
        Redirect(routes.GratorModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateTable(id: Long) = Action {
    GratorModule.findById(id).map{
      module:GratorModule => {
        //module.generateTable
        Redirect(routes.GratorModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateRow(id: Long) = Action {
    GratorModule.findById(id).map{
      module:GratorModule => {
        module.generateRow()
        Redirect(routes.GratorModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateViews(id: Long) = Action {
    GratorModule.findById(id).map{
      module:GratorModule => {
        module.generateViews()
        Redirect(routes.GratorModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}
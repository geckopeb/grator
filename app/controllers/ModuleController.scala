package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

object ModuleController extends Controller {
  def index = Action {
    //Ok(views.html.index("Your new module is ready."))
    val modules = ModuleRow.findAll

    Ok(views.html.module.index(modules))
  }

  val moduleForm = Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText,
        "application" -> longNumber
      )(ModuleRow.apply)(ModuleRow.unapply)
  )

  def insert = Action {
    Ok(views.html.module.insert(moduleForm, ApplicationRow.getOptions))
  }
  

  def detail(id: Long) = Action {
    ModuleRow.findById(id).map{
      module => Ok(views.html.module.detail(module))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    moduleForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.module.insert(formWithErrors,ApplicationRow.getOptions)),
      module => {
        val id = ModuleRow.save(module)
        Redirect(routes.ModuleController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    ModuleRow.findById(id).map{
      module:ModuleRow => Ok(views.html.module.edit(moduleForm.fill(module), ApplicationRow.getOptions, module))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    ModuleRow.findById(id).map{
      moduleRow => {
          ModuleRow.delete(moduleRow)
          Redirect(routes.ModuleController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    ModuleRow.findById(id).map{
      module => {
      moduleForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.module.edit(formWithErrors, ApplicationRow.getOptions, module)),
        module => {
          ModuleRow.update(module)
          Redirect(routes.ModuleController.detail(module.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    ModuleRow.findById(id).map{
      module:ModuleRow => {
        module.generateAll
        Redirect(routes.ModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateController(id: Long) = Action {
    ModuleRow.findById(id).map{
      module:ModuleRow => {
        module.generateController
        Redirect(routes.ModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateTable(id: Long) = Action {
    ModuleRow.findById(id).map{
      module:ModuleRow => {
        //module.generateTable
        Redirect(routes.ModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateRow(id: Long) = Action {
    ModuleRow.findById(id).map{
      module:ModuleRow => {
        module.generateRow()
        Redirect(routes.ModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateViews(id: Long) = Action {
    ModuleRow.findById(id).map{
      module:ModuleRow => {
        module.generateViews()
        Redirect(routes.ModuleController.detail(module.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}
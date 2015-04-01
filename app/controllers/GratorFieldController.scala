
package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

object GratorFieldController extends Controller {
  def index = Action {
    val gratorFields = GratorField.findAllWithRelateds

    Ok(views.html.gratorField.index(gratorFields))
  }

  val GratorFieldForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"moduleId" -> longNumber,
					"fieldType" -> nonEmptyText,
					"required" -> boolean,
					"relatedModuleId" -> optional(longNumber)

      )(GratorField.apply)(GratorField.unapply)
  )

  def insert = Action {
    val moduleOptions = GratorModule.getOptions
    val typeOptions = GratorField.getTypeOptions
    Ok(views.html.gratorField.insert(GratorFieldForm,moduleOptions,typeOptions))
  }
  

  def detail(id: Long) = Action {
    GratorField.findByIdWithRelateds(id).map{
      gratorField => Ok(views.html.gratorField.detail(gratorField))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    GratorFieldForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.gratorField.insert(formWithErrors, GratorModule.getOptions, GratorField.getTypeOptions)),
      gratorField => {
        val id = GratorField.save(gratorField)
        Redirect(routes.GratorFieldController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    GratorField.findById(id).map{
      field:GratorField => {
        val moduleOptions = GratorModule.getOptions
        val typeOptions = GratorField.getTypeOptions
        Ok(views.html.gratorField.edit(GratorFieldForm.fill(field),moduleOptions, typeOptions, field))
      }
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    GratorField.findById(id).map{
      gratorField => {
          GratorField.delete(gratorField)
          Redirect(routes.GratorFieldController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    GratorField.findById(id).map{
      field => {
      GratorFieldForm.bindFromRequest.fold(
        formWithErrors => {
          val moduleOptions = GratorModule.getOptions
          val typeOptions = GratorField.getTypeOptions
          BadRequest(views.html.gratorField.edit(formWithErrors, moduleOptions, typeOptions, field))
        },
        field => {
          GratorField.update(field)
          Redirect(routes.GratorFieldController.detail(field.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }
}

package controllers

import play.api._
import play.api.mvc._

import models.DB._

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.{Success, Failure}
import scala.concurrent.Future

class GratorFieldController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futureGratorField = GratorField.findAllWithRelateds
    futureGratorField.map{
      GratorField => Ok(views.html.GratorField.index(GratorField))
    }.recover {case ex: Exception => Ok("Fallo")}
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

  def insert = Action { implicit request =>
    Ok(views.html.GratorField.insert(GratorFieldForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      GratorField <- GratorField.findByIdWithRelateds(id)
      
    } yield ( (GratorField) )
    futureData.map{
      case ((Some(GratorField))) => Ok(views.html.GratorField.detail((GratorField)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    GratorFieldForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.GratorField.insert(formWithErrors))),
      GratorField => {
        val futureGratorField = GratorField.save(GratorField)

        futureGratorField.map{ result => Redirect(routes.GratorFieldController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorFieldController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorField.findByIdWithRelateds(id).map{
      case Some(GratorFieldTuple) => Ok(views.html.GratorField.edit(GratorFieldForm.fill(GratorFieldTuple._1),GratorFieldTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorField.findById(id).map{
      case Some(GratorField) => {
          GratorField.delete(GratorField)
          Redirect(routes.GratorFieldController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorField.findByIdWithRelateds(id).map{
      case Some(GratorFieldTuple) => {
        GratorFieldForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.GratorField.edit(formWithErrors,GratorFieldTuple)),
          GratorField => {
            GratorField.update(GratorField)
            Redirect(routes.GratorFieldController.detail(GratorField.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorField.findByQueryString(q)

    futureOptions.map{
      case options => {
        val GratorField = GratorField.toJsonRelatedCombo(options)
        Ok(GratorField).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

}

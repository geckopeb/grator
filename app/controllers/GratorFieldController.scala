
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
    val futuregratorField = GratorField.findAllWithRelateds
    futuregratorField.map{
      gratorField => Ok(views.html.gratorField.index(gratorField))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorFieldForm = Form(
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
    Ok(views.html.gratorField.insert(gratorFieldForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorField <- GratorField.findByIdWithRelateds(id)
      
    } yield ( (gratorField) )
    futureData.map{
      case ((Some(gratorField))) => Ok(views.html.gratorField.detail((gratorField)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorFieldForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorField.insert(formWithErrors))),
      gratorField => {
        val futuregratorField = GratorField.save(gratorField)

        futuregratorField.map{ result => Redirect(routes.GratorFieldController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorFieldController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorField.findByIdWithRelateds(id).map{
      case Some(gratorFieldTuple) => Ok(views.html.gratorField.edit(gratorFieldForm.fill(gratorFieldTuple._1),gratorFieldTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorField.findById(id).map{
      case Some(gratorField) => {
          GratorField.delete(gratorField)
          Redirect(routes.GratorFieldController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorField.findByIdWithRelateds(id).map{
      case Some(gratorFieldTuple) => {
        gratorFieldForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorField.edit(formWithErrors,gratorFieldTuple)),
          gratorField => {
            GratorField.update(gratorField)
            Redirect(routes.GratorFieldController.detail(gratorField.id.get))
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
        val gratorField = GratorField.toJsonRelatedCombo(options)
        Ok(gratorField).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def relatedDropdown = Action.async { implicit request =>
    val futureOptions = GratorField.findAll

    futureOptions.map{
      case options => {
        val gratorField = GratorField.toJsonRelatedCombo(options)
        Ok(gratorField).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  /* CUSTOM CODE */
  /* END CUSTOM CODE */
}

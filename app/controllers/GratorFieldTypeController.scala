
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

class GratorFieldTypeController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futuregratorFieldType = GratorFieldType.findAllWithRelateds
    futuregratorFieldType.map{
      gratorFieldType => Ok(views.html.gratorFieldType.index(gratorFieldType))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorFieldTypeForm = Form(
      mapping(
					"name" -> nonEmptyText,
					"id" -> optional(longNumber)

      )(GratorFieldType.apply)(GratorFieldType.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.gratorFieldType.insert(gratorFieldTypeForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorFieldType <- GratorFieldType.findByIdWithRelateds(id)
      
    } yield ( (gratorFieldType) )
    futureData.map{
      case ((Some(gratorFieldType))) => Ok(views.html.gratorFieldType.detail((gratorFieldType)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorFieldTypeForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorFieldType.insert(formWithErrors))),
      gratorFieldType => {
        val futuregratorFieldType = GratorFieldType.save(gratorFieldType)

        futuregratorFieldType.map{ result => Redirect(routes.GratorFieldTypeController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorFieldTypeController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorFieldType.findById(id).map{
      case Some(gratorFieldType) => Ok(views.html.gratorFieldType.edit(gratorFieldTypeForm.fill(gratorFieldType),gratorFieldType))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorFieldType.findById(id).map{
      case Some(gratorFieldType) => {
          GratorFieldType.delete(gratorFieldType)
          Redirect(routes.GratorFieldTypeController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorFieldType.findById(id).map{
      case Some(gratorFieldType) => {
        gratorFieldTypeForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorFieldType.edit(formWithErrors,gratorFieldType)),
          gratorFieldType => {
            GratorFieldType.update(gratorFieldType)
            Redirect(routes.GratorFieldTypeController.detail(gratorFieldType.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }
  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorFieldType.findByQueryString(q)

    futureOptions.map{
      case options => {
        val gratorFieldType = GratorFieldType.toJsonRelatedCombo(options)
        Ok(gratorFieldType).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def relatedDropdown = Action.async { implicit request =>
    val futureOptions = GratorFieldType.findAll

    futureOptions.map{
      case options => {
        val gratorFieldType = GratorFieldType.toJsonRelatedCombo(options)
        Ok(gratorFieldType).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  /* CUSTOM CODE */
  /* END CUSTOM CODE */
}

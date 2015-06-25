
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

class GratorAppController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futureGratorApp = GratorApp.findAllWithRelateds
    futureGratorApp.map{
      GratorApp => Ok(views.html.GratorApp.index(GratorApp))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val GratorAppForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"path" -> nonEmptyText

      )(GratorApp.apply)(GratorApp.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.GratorApp.insert(GratorAppForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      GratorApp <- GratorApp.findByIdWithRelateds(id)
      
    } yield ( (GratorApp) )
    futureData.map{
      case ((Some(GratorApp))) => Ok(views.html.GratorApp.detail((GratorApp)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    GratorAppForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.GratorApp.insert(formWithErrors))),
      GratorApp => {
        val futureGratorApp = GratorApp.save(GratorApp)

        futureGratorApp.map{ result => Redirect(routes.GratorAppController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorAppController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorApp.findById(id).map{
      case Some(GratorApp) => Ok(views.html.GratorApp.edit(GratorAppForm.fill(GratorApp),GratorApp))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorApp.findById(id).map{
      case Some(GratorApp) => {
          GratorApp.delete(GratorApp)
          Redirect(routes.GratorAppController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorApp.findById(id).map{
      case Some(GratorApp) => {
        GratorAppForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.GratorApp.edit(formWithErrors,GratorApp)),
          GratorApp => {
            GratorApp.update(GratorApp)
            Redirect(routes.GratorAppController.detail(GratorApp.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorApp.findByQueryString(q)

    futureOptions.map{
      case options => {
        val GratorApp = GratorApp.toJsonRelatedCombo(options)
        Ok(GratorApp).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

}

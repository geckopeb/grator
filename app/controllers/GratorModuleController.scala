
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

class GratorModuleController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futuregratorModule = GratorModule.findAllWithRelateds
    futuregratorModule.map{
      gratorModule => Ok(views.html.gratorModule.index(gratorModule))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorModuleForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"applicationId" -> longNumber

      )(GratorModule.apply)(GratorModule.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.gratorModule.insert(gratorModuleForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorModule <- GratorModule.findByIdWithRelateds(id)
      
    } yield ( (gratorModule) )
    futureData.map{
      case ((Some(gratorModule))) => Ok(views.html.gratorModule.detail((gratorModule)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorModuleForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorModule.insert(formWithErrors))),
      gratorModule => {
        val futuregratorModule = GratorModule.save(gratorModule)

        futuregratorModule.map{ result => Redirect(routes.GratorModuleController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorModuleController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorModule.findByIdWithRelateds(id).map{
      case Some(gratorModuleTuple) => Ok(views.html.gratorModule.edit(gratorModuleForm.fill(gratorModuleTuple._1),gratorModuleTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorModule.findById(id).map{
      case Some(gratorModule) => {
          GratorModule.delete(gratorModule)
          Redirect(routes.GratorModuleController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorModule.findByIdWithRelateds(id).map{
      case Some(gratorModuleTuple) => {
        gratorModuleForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorModule.edit(formWithErrors,gratorModuleTuple)),
          gratorModule => {
            GratorModule.update(gratorModule)
            Redirect(routes.GratorModuleController.detail(gratorModule.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorModule.findByQueryString(q)

    futureOptions.map{
      case options => {
        val gratorModule = GratorModule.toJsonRelatedCombo(options)
        Ok(gratorModule).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

}


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
    val futureGratorModule = GratorModule.findAllWithRelateds
    futureGratorModule.map{
      GratorModule => Ok(views.html.GratorModule.index(GratorModule))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val GratorModuleForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"applicationId" -> longNumber

      )(GratorModule.apply)(GratorModule.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.GratorModule.insert(GratorModuleForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      GratorModule <- GratorModule.findByIdWithRelateds(id)
      
    } yield ( (GratorModule) )
    futureData.map{
      case ((Some(GratorModule))) => Ok(views.html.GratorModule.detail((GratorModule)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    GratorModuleForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.GratorModule.insert(formWithErrors))),
      GratorModule => {
        val futureGratorModule = GratorModule.save(GratorModule)

        futureGratorModule.map{ result => Redirect(routes.GratorModuleController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorModuleController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorModule.findByIdWithRelateds(id).map{
      case Some(GratorModuleTuple) => Ok(views.html.GratorModule.edit(GratorModuleForm.fill(GratorModuleTuple._1),GratorModuleTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorModule.findById(id).map{
      case Some(GratorModule) => {
          GratorModule.delete(GratorModule)
          Redirect(routes.GratorModuleController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorModule.findByIdWithRelateds(id).map{
      case Some(GratorModuleTuple) => {
        GratorModuleForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.GratorModule.edit(formWithErrors,GratorModuleTuple)),
          GratorModule => {
            GratorModule.update(GratorModule)
            Redirect(routes.GratorModuleController.detail(GratorModule.id.get))
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
        val GratorModule = GratorModule.toJsonRelatedCombo(options)
        Ok(GratorModule).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

}

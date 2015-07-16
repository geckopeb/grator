
package controllers

import play.api._
import play.api.mvc._

import models.DB._
import it.grator.module_source.{App, AppFactory}

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.{Success, Failure}
import scala.concurrent.Future

class GratorAppController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futuregratorApp = GratorApp.findAllWithRelateds
    futuregratorApp.map{
      gratorApp => Ok(views.html.gratorApp.index(gratorApp))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorAppForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"path" -> nonEmptyText

      )(GratorApp.apply)(GratorApp.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.gratorApp.insert(gratorAppForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorApp <- GratorApp.findByIdWithRelateds(id)
      modules <- GratorModule.findByApplicationIdWithRelateds(id)
    } yield ( (gratorApp, modules) )
    futureData.map{
      case ((Some(gratorApp), modules: List[GratorModule])) => Ok(views.html.gratorApp.detail(gratorApp, modules))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorAppForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorApp.insert(formWithErrors))),
      gratorApp => {
        val futuregratorApp = GratorApp.save(gratorApp)

        futuregratorApp.map{ result => Redirect(routes.GratorAppController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorAppController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorApp.findById(id).map{
      case Some(gratorApp) => Ok(views.html.gratorApp.edit(gratorAppForm.fill(gratorApp),gratorApp))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorApp.findById(id).map{
      case Some(gratorApp) => {
          GratorApp.delete(gratorApp)
          Redirect(routes.GratorAppController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorApp.findById(id).map{
      case Some(gratorApp) => {
        gratorAppForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorApp.edit(formWithErrors,gratorApp)),
          gratorApp => {
            GratorApp.update(gratorApp)
            Redirect(routes.GratorAppController.detail(gratorApp.id.get))
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
        val gratorApp = GratorApp.toJsonRelatedCombo(options)
        Ok(gratorApp).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def relatedDropdown = Action.async { implicit request =>
    val futureOptions = GratorApp.findAll

    futureOptions.map{
      case options => {
        val gratorApp = GratorApp.toJsonRelatedCombo(options)
        Ok(gratorApp).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  /* CUSTOM CODE */

  def generateAll(id: Long) = Action.async {
    val futureData = for{
      gApp           <- GratorApp.findById(id)
      gModules       <- GratorModule.findAllByApplicationId(id)
      gFields        <- GratorField.findAllByApplicationId(id)
      gRelationships <- GratorRelationship.findAllByApplicationId(id)
      gSubpanels     <- GratorSubpanel.findAllByApplicationId(id)
      gFieldTypes    <- GratorFieldType.findAll
    } yield (gApp, gModules, gFields, gRelationships, gSubpanels, gFieldTypes)

    futureData.map{
      case ( Some(gApp: GratorApp), gModules: List[GratorModule], gFields: List[GratorField], gRelationships: List[GratorRelationship], gSubpanels: List[GratorSubpanel], gFieldTypes: List[GratorFieldType]) => {
        val app = AppFactory.construct(gApp.name, gApp.path, gModules, gFields, gRelationships, gSubpanels, gFieldTypes)
        app.generateAll()

        Redirect(routes.GratorAppController.detail(id))
      }
    }
  }

  /* END CUSTOM CODE */
}

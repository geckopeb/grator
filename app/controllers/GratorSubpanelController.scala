
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

class GratorSubpanelController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futuregratorSubpanel = GratorSubpanel.findAllWithRelateds
    futuregratorSubpanel.map{
      gratorSubpanel => Ok(views.html.gratorSubpanel.index(gratorSubpanel))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorSubpanelForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"toModule" -> longNumber,
					"fromModule" -> longNumber,
					"fromField" -> longNumber

      )(GratorSubpanel.apply)(GratorSubpanel.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.gratorSubpanel.insert(gratorSubpanelForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorSubpanel <- GratorSubpanel.findByIdWithRelateds(id)
      
    } yield ( (gratorSubpanel) )
    futureData.map{
      case ((Some(gratorSubpanel))) => Ok(views.html.gratorSubpanel.detail((gratorSubpanel)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorSubpanelForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorSubpanel.insert(formWithErrors))),
      gratorSubpanel => {
        val futuregratorSubpanel = GratorSubpanel.save(gratorSubpanel)

        futuregratorSubpanel.map{ result => Redirect(routes.GratorSubpanelController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorSubpanelController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorSubpanel.findByIdWithRelateds(id).map{
      case Some(gratorSubpanelTuple) => Ok(views.html.gratorSubpanel.edit(gratorSubpanelForm.fill(gratorSubpanelTuple._1),gratorSubpanelTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorSubpanel.findById(id).map{
      case Some(gratorSubpanel) => {
          GratorSubpanel.delete(gratorSubpanel)
          Redirect(routes.GratorSubpanelController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorSubpanel.findByIdWithRelateds(id).map{
      case Some(gratorSubpanelTuple) => {
        gratorSubpanelForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorSubpanel.edit(formWithErrors,gratorSubpanelTuple)),
          gratorSubpanel => {
            GratorSubpanel.update(gratorSubpanel)
            Redirect(routes.GratorSubpanelController.detail(gratorSubpanel.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }
  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorSubpanel.findByQueryString(q)

    futureOptions.map{
      case options => {
        val gratorSubpanel = GratorSubpanel.toJsonRelatedCombo(options)
        Ok(gratorSubpanel).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def relatedDropdown = Action.async { implicit request =>
    val futureOptions = GratorSubpanel.findAll

    futureOptions.map{
      case options => {
        val gratorSubpanel = GratorSubpanel.toJsonRelatedCombo(options)
        Ok(gratorSubpanel).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  /* CUSTOM CODE */
  /* END CUSTOM CODE */
}

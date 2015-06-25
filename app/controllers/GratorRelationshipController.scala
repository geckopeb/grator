
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

class GratorRelationshipController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action.async { implicit request =>
    val futureGratorRelationship = GratorRelationship.findAllWithRelateds
    futureGratorRelationship.map{
      GratorRelationship => Ok(views.html.GratorRelationship.index(GratorRelationship))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val GratorRelationshipForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"relType" -> nonEmptyText,
					"primaryModuleId" -> longNumber,
					"primaryModuleLabel" -> nonEmptyText,
					"primaryModuleSubpanel" -> nonEmptyText,
					"relatedModuleId" -> longNumber,
					"relatedModuleLabel" -> nonEmptyText,
					"relatedModuleSubpanel" -> nonEmptyText

      )(GratorRelationship.apply)(GratorRelationship.unapply)
  )

  def insert = Action { implicit request =>
    Ok(views.html.GratorRelationship.insert(GratorRelationshipForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      GratorRelationship <- GratorRelationship.findByIdWithRelateds(id)
      
    } yield ( (GratorRelationship) )
    futureData.map{
      case ((Some(GratorRelationship))) => Ok(views.html.GratorRelationship.detail((GratorRelationship)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    GratorRelationshipForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.GratorRelationship.insert(formWithErrors))),
      GratorRelationship => {
        val futureGratorRelationship = GratorRelationship.save(GratorRelationship)

        futureGratorRelationship.map{ result => Redirect(routes.GratorRelationshipController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorRelationshipController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorRelationship.findByIdWithRelateds(id).map{
      case Some(GratorRelationshipTuple) => Ok(views.html.GratorRelationship.edit(GratorRelationshipForm.fill(GratorRelationshipTuple._1),GratorRelationshipTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorRelationship.findById(id).map{
      case Some(GratorRelationship) => {
          GratorRelationship.delete(GratorRelationship)
          Redirect(routes.GratorRelationshipController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorRelationship.findByIdWithRelateds(id).map{
      case Some(GratorRelationshipTuple) => {
        GratorRelationshipForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.GratorRelationship.edit(formWithErrors,GratorRelationshipTuple)),
          GratorRelationship => {
            GratorRelationship.update(GratorRelationship)
            Redirect(routes.GratorRelationshipController.detail(GratorRelationship.id.get))
          }
        )
      }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  



  def relatedCombo(q: String) = Action.async { implicit request =>
    val futureOptions = GratorRelationship.findByQueryString(q)

    futureOptions.map{
      case options => {
        val GratorRelationship = GratorRelationship.toJsonRelatedCombo(options)
        Ok(GratorRelationship).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

}

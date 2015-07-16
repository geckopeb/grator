
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
    val futuregratorRelationship = GratorRelationship.findAllWithRelateds
    futuregratorRelationship.map{
      gratorRelationship => Ok(views.html.gratorRelationship.index(gratorRelationship))
    }.recover {case ex: Exception => Ok("Fallo")}
  }

  val gratorRelationshipForm = Form(
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
    Ok(views.html.gratorRelationship.insert(gratorRelationshipForm))
  }

  def detail(id: Long) = Action.async { implicit request =>
    val futureData = for {
      gratorRelationship <- GratorRelationship.findByIdWithRelateds(id)
      
    } yield ( (gratorRelationship) )
    futureData.map{
      case ((Some(gratorRelationship))) => Ok(views.html.gratorRelationship.detail((gratorRelationship)))
      case _ => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def save = Action.async { implicit request =>
    gratorRelationshipForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.gratorRelationship.insert(formWithErrors))),
      gratorRelationship => {
        val futuregratorRelationship = GratorRelationship.save(gratorRelationship)

        futuregratorRelationship.map{ result => Redirect(routes.GratorRelationshipController.detail(result))}.recover{
          case ex: Exception => Redirect(routes.GratorRelationshipController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async{ implicit request =>
    GratorRelationship.findByIdWithRelateds(id).map{
      case Some(gratorRelationshipTuple) => Ok(views.html.gratorRelationship.edit(gratorRelationshipForm.fill(gratorRelationshipTuple._1),gratorRelationshipTuple))
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo")}
  }

  def delete(id: Long) = Action.async{
    implicit request =>
    GratorRelationship.findById(id).map{
      case Some(gratorRelationship) => {
          GratorRelationship.delete(gratorRelationship)
          Redirect(routes.GratorRelationshipController.index())
        }
      case None => NotFound
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def update(id: Long) = Action.async{ implicit request =>
    GratorRelationship.findByIdWithRelateds(id).map{
      case Some(gratorRelationshipTuple) => {
        gratorRelationshipForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.gratorRelationship.edit(formWithErrors,gratorRelationshipTuple)),
          gratorRelationship => {
            GratorRelationship.update(gratorRelationship)
            Redirect(routes.GratorRelationshipController.detail(gratorRelationship.id.get))
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
        val gratorRelationship = GratorRelationship.toJsonRelatedCombo(options)
        Ok(gratorRelationship).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  def relatedDropdown = Action.async { implicit request =>
    val futureOptions = GratorRelationship.findAll

    futureOptions.map{
      case options => {
        val gratorRelationship = GratorRelationship.toJsonRelatedCombo(options)
        Ok(gratorRelationship).as("application/json")
      }
    }.recover { case ex: Exception => Ok("Fallo") }
  }

  /* CUSTOM CODE */
  /* END CUSTOM CODE */
}

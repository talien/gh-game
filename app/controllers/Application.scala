package controllers

import play.api._
import play.api.mvc._
import securesocial.core.{IdentityId, UserService, Identity, Authorization}
import securesocial.controllers.TemplatesPlugin

object Application extends Controller with securesocial.core.SecureSocial {
  
  def index = SecuredAction(WithProvider("github")) { implicit request => {
      Ok(views.html.index())
    }
  }
}

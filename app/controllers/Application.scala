package controllers

import play.api._
import play.api.mvc._
import securesocial.core.{IdentityId, UserService, Identity, Authorization, OAuth2Info}
import securesocial.controllers.TemplatesPlugin
import play.api.libs.ws._
import scala.concurrent.Future
import scala.concurrent._
import scala.concurrent.duration._

object Application extends Controller with securesocial.core.SecureSocial {
  
  def index = SecuredAction { implicit request => {
      implicit val context = scala.concurrent.ExecutionContext.Implicits.global

      val accessToken = request.user.oAuth2Info match {
        case Some(info) => info.accessToken
        case None => ""
      }
            
      val futureResult: Future[Seq[String]] = WS.url("https://api.github.com/user/repos").withQueryString("access_token" -> accessToken).get().map {
          response =>
              (response.json \\ "name") map (_.as[String])
      }
      val repos = Await.result(futureResult, 30 seconds)
      Ok(views.html.index(request.user, repos))
    }
  }

  def repo(repositoryName: String) = SecuredAction { implicit request => {
      Ok(views.html.repo(repositoryName))
    }
  }
}

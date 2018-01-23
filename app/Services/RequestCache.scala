package Services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Entities.EntityDefinitions.{MembershipType, MembershipTypeExp, ProgramType, Rating}
import Logic.DateLogic
import Services.Authentication.{PublicUserType, RootUserType, UserType}
import play.api.mvc.{Cookies, Headers}

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
class RequestCache private[RequestCache] (
  val authenticatedUserName: String,
  val authenticatedUserType: UserType
) {
  private val self = this
  val pb: PersistenceBroker = new OracleBroker(this)
  val cb: CacheBroker = new RedisBroker

  // TODO: some way to confirm that things like this have no security on them (regardless of if we pass or fail in this req)
  // TODO: dont do this every request.
  object cachedEntities {
    lazy val programTypes: List[ProgramType] = pb.getAllObjectsOfClass(ProgramType)
    lazy val membershipTypes: List[MembershipType] = {
      pb.getAllObjectsOfClass(MembershipType).map(m => {
        m.references.program.findOneInCollection(programTypes)
        m
      })
    }
    lazy val membershipTypeExps: List[MembershipTypeExp] = {
      pb.getAllObjectsOfClass(MembershipTypeExp).map(me => {
        me.references.membershipType.findOneInCollection(membershipTypes)
        me
      })
    }
    lazy val ratings: List[Rating] = pb.getAllObjectsOfClass(Rating)
  }

  object logic {
    val dateLogic: DateLogic = new DateLogic(self)
  }
}

object RequestCache {
  // TODO: better way to handle requests authenticated against multiple mechanisms?
  def construct(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): RequestCache = {
    println("\n\n====================================================")
    println("====================================================")
    println("====================================================")
    println("Request received: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    // For all the enabled user types (besides public), see if the request is authenticated against any of them.
    val ret: Option[RequestCache] = PermissionsAuthority.allowableUserTypes.get
      .filter(_ != PublicUserType)
      .foldLeft(None: Option[RequestCache])((retInner: Option[RequestCache], ut: UserType) => retInner match {
        case Some(x) => Some(x)
        case None => ut.getAuthenticatedUsernameInRequest(requestHeaders, requestCookies, rootCB, apexToken) match {
          case None => None
          case Some(x: String) => {
            println("AUTHENTICATION:  Request is authenticated as " + ut)
            Some(new RequestCache(x, ut))
          }
        }
      })

    ret match {
      case Some(x) => x
      case None => {
        println("AUTHENTICATION:  No auth mechanisms matched; this is a Public request")
        new RequestCache(PublicUserType.publicUserName, PublicUserType)
      }
    }
  }

  def constructFromSuperiorAuth(
    rc: RequestCache,
    desiredUserType: UserType,
    desiredUserName: String
  ): Option[RequestCache] = {
    desiredUserType.getAuthenticatedUsernameFromSuperiorAuth(rc, desiredUserName) match {
      case Some(s: String) => {
        println("@@@ Successfully downgraded to " + desiredUserType)
        Some(new RequestCache(s, desiredUserType))
      }
      case None => {
        println("@@@ Unable to downgrade auth to " + desiredUserType)
        None
      }
    }
  }

  lazy private[Services] val getRootRC: RequestCache = new RequestCache("", RootUserType)
}
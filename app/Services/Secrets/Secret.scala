package Services.Secrets

import CbiUtil.Initializable
import Services.RequestCache

class Secret (auth: (RequestCache => Boolean)) {
  val secret = new Initializable[String]
  def get(rc: RequestCache): String = {
    if(auth(rc)) secret.get
    else throw new Exception("Unauthorized access to secret")
  }
  def set(s: String): String = secret.set(s)
}
package org.sailcbi.APIServer.Services

import com.coleji.framework.Core.RequestCache
import org.sailcbi.APIServer.CbiUtil.Initializable

class Secret[T](auth: (RequestCache => Boolean)) {
	val secret = new Initializable[T]

	def get(rc: RequestCache): T = {
		if (auth(rc)) secret.get
		else throw new Exception("Unauthorized access to secret")
	}

	def set(s: T): T = secret.set(s)

	def setImmediate(s: T): Secret[T] = {
		secret.set(s)
		this
	}
}
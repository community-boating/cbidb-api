package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import play.api.mvc.Result

import scala.concurrent.{ExecutionContext, Future}

object MemberMaybeOrProtoPersonRequestCache {

	def getRCWithProtoPersonRC(pa: PermissionsAuthority, parsed: ParsedRequest, block: (PersonRequestBaseCache, ProtoPersonRequestCache) => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		pa.withRequestCache(MemberMaybeRequestCache)(None, parsed, rcMM => {
			pa.withRequestCache(ProtoPersonRequestCache)(None, parsed, rcPP => {
				val rc = if(rcMM.getAuthedPersonId.isDefined) rcMM else rcPP
				block(rc, rcPP)
			})
		})
	}

	def getRC(pa: PermissionsAuthority, parsed: ParsedRequest, block: PersonRequestBaseCache => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		pa.withRequestCache(MemberMaybeRequestCache)(None, parsed, rcMM => {
			if(rcMM.getAuthedPersonId.isDefined) {
				block(rcMM)
			}else{
				pa.withRequestCache(ProtoPersonRequestCache)(None, parsed, rcPP => {
					block(rcPP)
				})
			}
		})
	}

}

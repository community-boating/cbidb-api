package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.Services.{PermissionsAuthoritySecrets, RequestCache}

abstract class NonMemberRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends RequestCache(userName, secrets)

package org.sailcbi.APIServer.Services.Authentication

abstract class NonMemberUserType(override val userName: String) extends UserType(userName)

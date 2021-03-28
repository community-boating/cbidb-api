package org.sailcbi.APIServer.Server

import com.coleji.framework.Core.Boot.ServerBootLoaderLive
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class CBILiveBootLoader @Inject()(lifecycle: ApplicationLifecycle) {
	new ServerBootLoaderLive(lifecycle)
}

package org.sailcbi.APIServer.Services.Shell

object ShellManager {
	private[Services] def execute(command: String) = new CommandWrapper(command, None, None, None, None)

	private[Services] def execute(command: String, cleanupCommand: Option[String], mainMaxWaitMillis: Option[Integer], cleanupMaxWaitMillis: Option[Integer], stdintext: Option[String]) =
		new CommandWrapper(command, cleanupCommand, mainMaxWaitMillis, cleanupMaxWaitMillis, stdintext)
}

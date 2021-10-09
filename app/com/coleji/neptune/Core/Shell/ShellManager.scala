package com.coleji.neptune.Core.Shell

object ShellManager {
	private[Core] def execute(command: String) = new CommandWrapper(command, None, None, None, None)

	private[Core] def execute(command: String, cleanupCommand: Option[String], mainMaxWaitMillis: Option[Integer], cleanupMaxWaitMillis: Option[Integer], stdintext: Option[String]) =
		new CommandWrapper(command, cleanupCommand, mainMaxWaitMillis, cleanupMaxWaitMillis, stdintext)
}

package com.coleji.framework.Core.Shell

import com.coleji.framework.Util.Initializable

import java.io.IOException

class ProcessWrapper private[Shell](command: String, maxWaitMillis: Option[Integer], stdintext: Option[String]) {
	val p = new Initializable[Process]

	// Execute a shell command and manage the resulting process
	try {
		p.set(Runtime.getRuntime.exec(command))
		if (stdintext.isDefined) try {
			val stdin = p.get.getOutputStream
			stdin.write(stdintext.get.getBytes)
			stdin.write(10) // newline

			stdin.write(3) // EOT, must be on its own line

			stdin.close()
		} catch {
			case e: IOException =>
				// TODO Auto-generated catch block
				e.printStackTrace()
		}
		if (maxWaitMillis.isEmpty) { // wait forever
			p.get.waitFor
		}
		else { // poll every 1ms to see if its done yet
			val start = System.currentTimeMillis
			val end = start + maxWaitMillis.get
			while ( {
				System.currentTimeMillis < end && this.isRunning
			}) Thread.sleep(1)
			if (this.isRunning) { // exceeded our timeout and its still running; kill it
				this.destroy()
				throw new Exception("The following command timed out after " + maxWaitMillis + " milliseconds: \n" + command)
			}
		}
	} catch {
		case e@(_: IOException | _: InterruptedException) =>
			e.printStackTrace()
	}


	def isRunning: Boolean = {
		try {
			p.get.exitValue
			false
		} catch {
			case _: Exception =>
				true
		}
	}

	private def destroy(): Unit = {
		p.get.destroy()
	}

	def getExitValue: Int = p.get.exitValue

	def unwrap: Process = p.get
}

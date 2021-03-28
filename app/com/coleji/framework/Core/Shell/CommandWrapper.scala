package com.coleji.framework.Core.Shell

import com.coleji.framework.Util.Initializable

import java.io.{BufferedReader, IOException, InputStreamReader, StringReader}

class CommandWrapper private[Shell](
	command: String,
	cleanupCommand: Option[String],
	mainMaxWaitMillis: Option[Integer],
	cleanupMaxWaitMillis: Option[Integer],
	stdintext: Option[String]
) {
	val main = new Initializable[ProcessWrapper]
	val cleanup = new Initializable[ProcessWrapper]
	val outputStream = new Initializable[StringBuilder]

	try {
		main.set(new ProcessWrapper(command, mainMaxWaitMillis, stdintext))
		outputStream.set(new StringBuilder)
		val br = new BufferedReader(new InputStreamReader(main.get.unwrap.getInputStream))
		br.lines().forEach(line => {
			outputStream.get.append(line + '\n')
		})
		br.close()
	} catch {
		case _: Exception =>
			System.out.println("process timed out")
			// if the process times out and there is a cleanup process, execute it
			if (cleanupCommand.isDefined) try
				cleanup.set(new ProcessWrapper(cleanupCommand.get, cleanupMaxWaitMillis, null))
			catch {
				case e1: Exception =>
					// TODO: some kind of AttemptedCleanupAndThatFailedTooException
					e1.printStackTrace()
			}
		case e: IOException =>
			// TODO Auto-generated catch block
			e.printStackTrace()
	}

	def getMain: Process = main.get.unwrap

	def getMainOutputAsBufferedReader = new BufferedReader(new StringReader(outputStream.toString))

	def getMainOutputAsString: String = outputStream.toString

	def getExitValue: Int = main.get.getExitValue

	// first line is 0
	def getMainOutputLine(lineNumber: Int): String = {
		val outputReader = this.getMainOutputAsBufferedReader
		var line: String = null
		try {
			(0 to (lineNumber + 1)).foreach(_ => {
				line = outputReader.readLine
			})
			outputReader.close()
			line
		} catch {
			case e: IOException =>
				// TODO Auto-generated catch block
				e.printStackTrace()
				""
		}
	}
}

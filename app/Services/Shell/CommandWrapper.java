package Services.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import Services.Shell.Exception.ProcessTimeoutException;

//TODO: rewrite with ProcessBuilder
// http://stackoverflow.com/questions/3643939/java-process-with-input-output-stream/3644288#3644288


public class CommandWrapper {
	ProcessWrapper main, cleanup;
	StringBuilder outputStream;
	public CommandWrapper(String command, String cleanupCommand, Integer mainMaxWaitMillis, Integer cleanupMaxWaitMillis, String stdintext) {
		try {
			main = new ProcessWrapper(command, mainMaxWaitMillis, stdintext);
			outputStream = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(main.unwrap().getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				outputStream.append(line + '\n');
			}
			br.close();
		} catch (ProcessTimeoutException e) {
			System.out.println("process timed out");
			// if the process times out and there is a cleanup process, execute it
			if (cleanupCommand != null) {
				try {
					cleanup = new ProcessWrapper(cleanupCommand, cleanupMaxWaitMillis, null);
				} catch (ProcessTimeoutException e1) {
					// TODO: some kind of AttemptedCleanupAndThatFailedTooException
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Process getMain() {
		return main.unwrap();
	}
	/*
	public String getMainOutput() {
		StringBuffer output = new StringBuffer();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getMain().getInputStream()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reader = new BufferedReader(new InputStreamReader(this.getMain().getErrorStream()));
		try {
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output.toString();
	}*/
	public BufferedReader getMainOutputAsBufferedReader() {
		return new BufferedReader(new StringReader(outputStream.toString()));
	}
	
	public String getMainOutputAsString() {
		return outputStream.toString();
	}
	
	public int getExitValue(){
		return main.getExitValue();
	}
	
	// first line is 0
	public String getMainOutputLine(int lineNumber) {
		BufferedReader outputReader = this.getMainOutputAsBufferedReader();
		int i = 0;
		String line = null;
		try {
			while (lineNumber + 1 > i++) {
				line = outputReader.readLine();
			}
			outputReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return line;
	}
}
